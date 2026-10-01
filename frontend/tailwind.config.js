/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        navy: {
          DEFAULT: '#13293D',
          dark: '#0B1B2B',
          light: '#1B3B5A',
        },
        teal: {
          DEFAULT: '#17C3B2',
          dark: '#0E7C72',
          light: '#2CD9C8',
        },
        surface: {
          soft: '#F4F8F9',
          dark: '#0B1B2B',
          card: '#13293D',
        },
        text: {
          light: '#F4F8F9',
          main: '#E8F1F2',
          muted: '#8AA3B8',
          dark: '#0B1B2B',
        },
        semantic: {
          success: '#10B981', // green
          warning: '#F59E0B', // amber
          danger: '#EF4444', // red
          critical: '#9F1239', // deep red/magenta
        }
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', '-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'sans-serif'],
      }
    },
  },
  plugins: [],
}
