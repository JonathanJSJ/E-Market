import { fetchServer } from '@/services/fetchServer';
import { UserTokenDecoded } from '@/types/database';
import { NextRequest, NextResponse } from 'next/server';

const validationTokenMiddleware = async (
  req: NextRequest,
  autorizationList: string[]
) => {
  const authorizationHeader =
    req.headers.get('authorization') ||
    req.cookies.get('nextauth.token.ds4')?.value;

  if (!authorizationHeader) {
    return NextResponse.json({ error: 'Não autorizado' }, { status: 401 });
  }

  try {
    const response = await fetchServer('/api/auth/token', {
      headers: {
        Authorization: authorizationHeader,
      },
      method: 'POST',
    });
    const userInfo: UserTokenDecoded = await response.json();
    const responseData = await fetchServer(
      '/api/database/groups/groupsFromUser',
      {
        headers: {
          Authorization: authorizationHeader,
          method: 'POST',
        },
      }
    );

    const groupsData = await responseData.json();

    if (!hasCommonItem(groupsData, autorizationList)) {
      return NextResponse.json(
        { error: 'Não autorizado para essa funcionalidade' },
        { status: 401 }
      );
    }

    return { userInfo, authorized: true };
  } catch (error) {
    return NextResponse.json({ error: 'Não autorizado' }, { status: 401 });
  }
};

function hasCommonItem(list1: string[], list2: string[]): boolean {
  if (list2.includes('ALL')) {
    return true;
  }

  return list1.some((item) => list2.includes(item));
}

export const withValidation = (
  handler: Function,
  autorizationList: string[]
) => {
  return async (req: NextRequest) => {
    const validationResponse = await validationTokenMiddleware(
      req,
      autorizationList
    );

    if (
      'authorized' in validationResponse &&
      validationResponse.authorized &&
      validationResponse.userInfo
    ) {
      return handler(req, validationResponse.userInfo);
    } else {
      return validationResponse;
    }
  };
};
