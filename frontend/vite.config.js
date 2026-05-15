import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    host: '0.0.0.0',
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8090',
        changeOrigin: true
      }
    }
  },
  build: {
    target: 'es2015',
    cssTarget: 'chrome80',
    minify: 'esbuild',
    esbuild: {
      drop: ['console', 'debugger']
    },
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (id.includes('node_modules')) {
            if (id.includes('vue/') || id.includes('vue-router')) {
              return 'vue-vendor'
            }
            if (id.includes('axios')) {
              return 'axios-vendor'
            }
            if (id.includes('echarts') || id.includes('zrender')) {
              return undefined
            }
            return 'vendor'
          }
        },
        chunkFileNames: 'assets/js/[name]-[hash].js',
        entryFileNames: 'assets/js/[name]-[hash].js',
        assetFileNames: 'assets/[ext]/[name]-[hash].[ext]'
      }
    },
    chunkSizeWarningLimit: 500,
    assetsInlineLimit: 4096,
    sourcemap: false,
    reportCompressedSize: false
  },
  css: {
    devSourcemap: false,
    modules: {
      localsConvention: 'camelCase'
    }
  },
  optimizeDeps: {
    include: ['vue', 'vue-router', 'axios']
  }
})
