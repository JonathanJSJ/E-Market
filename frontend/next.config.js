/** @type {import('next').NextConfig} */
const nextConfig = {
  swcMinify: true,
  reactStrictMode: true,
  images: {
    remotePatterns: [{
      hostname: 'imgur.com'
    }],
  },
  modularizeImports: {
    "@mui/icons-material": {
      transform: "@mui/icons-material/{{member}}",
    },
  },
  typescript: {
    ignoreBuildErrors: true,
  },
  async redirects() {
    return [
      {
        source: '/',
        destination: '/start',
        permanent: true,
      },
    ];
  },

}

module.exports = nextConfig