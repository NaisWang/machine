let path = require('path')

let htmlWebpackPlugin = require('html-webpack-plugin')
const {VueLoaderPlugin} = require('vue-loader')

module.exports = {
  mode: 'development',
  entry: path.resolve(__dirname, 'src/main.js'),
  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: 'bundle.js'
  },
  plugins: [
    new htmlWebpackPlugin({
      template: path.resolve(__dirname, 'src/index.html'),
      filename: 'index.html'
    }),
    new VueLoaderPlugin()
  ],
  module: {
    rules: [
      {test: /\.css$/, use: ['style-loader', 'css-loader']},
      {test: /\.scss$/, use: ['style-loader', 'css-loader', 'sass-loader']},
      {test: /\.vue$/, use: 'vue-loader'},
      {test: /\.js$/, use: 'babel-loader', exclude: /node_modules/},
      {test: /\.(png|jpg|gif|bmp?jpeg)$/, use: 'url-loader'},
      {test: /\.(ttf|eot|svg|woff|woff2)$/, use: 'url-loader'}
    ]
  }
}