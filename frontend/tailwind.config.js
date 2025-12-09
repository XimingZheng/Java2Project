/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'stackoverflow': {
          orange: '#F48024',
          black: '#242729',
          blue: '#0077CC',
        }
      }
    },
  },
  plugins: [],
}
