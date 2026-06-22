import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import viteCompression from 'vite-plugin-compression'

function removeCrossoriginPlugin() {
  return {
    name: 'remove-crossorigin',
    transformIndexHtml: {
      order: 'after',
      handler(html) {
        return html
          .replace(/ crossorigin/g, '')
          .replace(/<link rel="modulepreload"[^>]*>/g, '')
      }
    }
  }
}

function bypassHostCheck() {
  return {
    name: 'bypass-host-check',
    configureServer(server) {
      server.middlewares.use((req, res, next) => {
        const host = req.headers.host
        if (host && host.includes('cpolar.cn')) {
          req.headers.host = 'localhost:3000'
        }
        next()
      })
    }
  }
}

export default defineConfig({
  base: './',
  plugins: [
    vue(),
    removeCrossoriginPlugin(),
    bypassHostCheck(),
    viteCompression({
      algorithm: 'gzip',
      threshold: 1024,
      deleteOriginFile: false
    }),
    viteCompression({
      algorithm: 'brotliCompress',
      threshold: 1024,
      deleteOriginFile: false
    })
  ],
  server: {
    host: '0.0.0.0',
    port: 3000,
    allowedHosts: true,
    proxy: {
      '/api': {
        target: 'http://192.168.146.128:8090',
        changeOrigin: true
      },
      '/images/news': {
        target: 'http://192.168.146.128:8090',
        changeOrigin: true
      }
    }
  },
  build: {
    target: 'es2015',
    cssTarget: 'chrome80',
    minify: 'esbuild',
    cssMinify: 'esbuild',
    cssCodeSplit: true,
    modulePreload: false,
    esbuild: {
      drop: ['debugger'],
      pure: ['console.log', 'console.info', 'console.warn', 'console.debug']
    },
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (id.includes('node_modules')) {
            if (id.includes('echarts') || id.includes('zrender')) {
              return 'echarts-vendor'
            }
            if (id.includes('markdown-it') || id.includes('dompurify')) {
              return 'markdown-vendor'
            }
            if (id.includes('axios')) {
              return 'axios-vendor'
            }
            if (id.includes('vue') || id.includes('pinia') || id.includes('@vueuse')) {
              return 'vue-vendor'
            }
            return 'vendor'
          }
          if (id.includes('/views/')) {
            const match = id.match(/\/views\/(?:portal|dashboard)\/(\w+)\.vue$/)
            if (match) return `page-${match[1].toLowerCase()}`
          }
        },
        chunkFileNames: 'assets/js/[name]-[hash].js',
        entryFileNames: 'assets/js/[name]-[hash].js',
        assetFileNames: 'assets/[ext]/[name]-[hash].[ext]'
      }
    },
    chunkSizeWarningLimit: 1000,
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
