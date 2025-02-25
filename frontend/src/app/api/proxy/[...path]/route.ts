import { NextRequest, NextResponse } from 'next/server';

const BACKEND_URL = process.env.BACKEND_HOST;

function convertHeadersToObject(headers: Headers) {
  const headersObject: Record<string, string> = {};
  headers.forEach((value, key) => {
    headersObject[key] = value;
  });
  return headersObject;
}

export async function GET(req: NextRequest) {
  const { pathname, search } = new URL(req.url);
  const apiPath = pathname.replace('/api/proxy', '');

  try {
    const response = await fetch(`${BACKEND_URL}${apiPath}${search}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: req.headers.get('authorization') || '',
      },
    });

    const data = await response.json();
    return NextResponse.json(data, { status: response.status });
  } catch (error) {
    console.error('Erro ao fazer proxy para o backend:', error);
    return NextResponse.json(
      { error: 'Erro ao fazer proxy para o backend' },
      { status: 500 }
    );
  }
}

export async function POST(req: NextRequest) {
  const { pathname, search } = new URL(req.url);
  const contentLength = req.headers.get('content-length') || 0;
  const apiPath = pathname.replace('/api/proxy', '');
  const bodyReq = Number(contentLength) > 0 ? await req.json() : false;

  let response: Response | null;
  try {
    if (bodyReq) {
      response = await fetch(`${BACKEND_URL}${apiPath}${search}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: req.headers.get('authorization') || '',
        },
        body: JSON.stringify(bodyReq),
      });
    } else {
      response = await fetch(`${BACKEND_URL}${apiPath}${search}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: req.headers.get('authorization') || '',
        },
      });
    }

    const contentType = response.headers.get('Content-Type') || '';
    if (contentType.includes('application/json')) {
      const data = await response.json();
      return NextResponse.json(data, { status: response.status });
    } else {
      const text = await response.text();
      return NextResponse.json({ message: text }, { status: response.status });
    }
  } catch (error) {
    console.error('Erro ao fazer proxy para o backend:', error);
    return NextResponse.json(
      { error: 'Erro ao fazer proxy para o backend' },
      { status: 500 }
    );
  }
}

export async function PUT(req: NextRequest) {
  const { pathname, search } = new URL(req.url);
  const apiPath = pathname.replace('/api/proxy', '');
  const bodyReq = req.body;

  let response: Response | null;
  try {
    if (bodyReq) {
      response = await fetch(`${BACKEND_URL}${apiPath}${search}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: req.headers.get('authorization') || '',
        },
        body: JSON.stringify(bodyReq),
      });
    } else {
      response = await fetch(`${BACKEND_URL}${apiPath}${search}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: req.headers.get('authorization') || '',
        },
      });
    }

    return NextResponse.json(response, { status: response.status });
  } catch (error) {
    console.error('Erro ao fazer proxy para o backend:', error);
    return NextResponse.json(
      { error: 'Erro ao fazer proxy para o backend' },
      { status: 500 }
    );
  }
}

export async function DELETE(req: NextRequest) {
  const { pathname, search } = new URL(req.url);
  const apiPath = pathname.replace('/api/proxy', '');

  const headersRequest: { Authorization: string; 'Content-Type': string } = {
    Authorization: req.headers.get('authorization') || '',
    'Content-Type': 'application/json',
  };

  try {
    const response = await fetch(`${BACKEND_URL}${apiPath}${search}`, {
      headers: headersRequest,
      method: 'DELETE',
    });

    return NextResponse.json(response, { status: response.status });
  } catch (error) {
    console.error('Erro ao fazer proxy para o backend:', error);
    return NextResponse.json(
      { error: 'Erro ao fazer proxy para o backend' },
      { status: 500 }
    );
  }
}
