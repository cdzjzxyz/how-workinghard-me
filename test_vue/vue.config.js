const { defineConfig } = require('@vue/cli-service')
module.exports = {
  devServer: {
    host: '0.0.0.0',
    allowedHosts: 'all', // 允许所有host访问
    // disableHostCheck: true, // 新版已废弃，用 allowedHosts
    port: 8086, // 你的端口号，和ngrok一致
    https: false,
    proxy: {
      '/depts': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/login': {
        target: 'http://localhost:8080', // 你的后端端口
        changeOrigin: true
      },
      '/register': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/emps': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/ai': {
        target: 'http://localhost:8080', // 你的后端地址
        changeOrigin: true
      }
    },
    hot: false,
    liveReload: false,
    client: false
  },
  lintOnSave: false
}
