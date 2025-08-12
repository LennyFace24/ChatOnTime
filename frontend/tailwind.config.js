/** @type {import('tailwindcss').Config} */
module.exports =  {
  content: [
    "./index.html",
    "./src/**/*.{html,ts,vue,js}"],
  theme: {
    extend: {
      colors:{
        'azure':'#F0FFFF',
      }
    },
  },
  plugins: [],
}

