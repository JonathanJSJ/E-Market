import { NextResponse } from 'next/server';

export async function POST(request: Request) {
  const body = await request.json();
  const host = process.env.BACKEND_HOST;

  const backendResponse = await fetch(`${host}/api/auth/register`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(body),
  });

  const data = await backendResponse.json();
  return NextResponse.json(data, { status: backendResponse.status });
}
