import { defineConfig } from 'cypress';
import dotenv from 'dotenv';

dotenv.config({ path: '.env' });

export default defineConfig({
  e2e: {
    viewportWidth: 1920,
    viewportHeight: 1080,
    baseUrl: `http://localhost`,
    setupNodeEvents(on, config) {
      return config;
    },
  },

  component: {
    devServer: {
      framework: 'next',
      bundler: 'webpack',
    },
  },
});
