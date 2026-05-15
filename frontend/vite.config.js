import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

function preloadPlugin() {
  return {
    name: 'preload-critical-assets',
    transformIndexHtml: {
      enforce: 'after',
      transform(html, ctx) {
        if (!ctx.bundle) return html
        const links = []
        for (const [, chunk] of Object.entries(ctx.bundle)) {
          if (chunk.type === 'chunk' && chunk.isEntry) {
            links.push(`<link rel="preload" href="/${chunk.fileName}" as="script" crossorigin>`)
          }
        }
        for (const [, chunk] of Object.entries(ctx.bundle)) {
          if (chunk.type === 'asset' && chunk.fileName.endsWith('.css')) {
            if (chunk.fileName.includes('index-') || chunk.fileName.includes('PortalLayout-')) {
              links.push(`<link rel="preload" href="/${chunk.fileName}" as="style">`)
            }
          }
        }
        if (links.length === 0) return html
        return html.replace('</head>', links.join('\n') + '\n</head>')
      }
    }
  }
}

export default defineConfig({
  plugins: [vue(), preloadPlugin()],
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
