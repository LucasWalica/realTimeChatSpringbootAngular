/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        // Colores base para que funcionen bg-background y text-foreground
        background: "#0a0a0f",
        foreground: "#f5f5f7",
        
        // El color border para que funcione border-border
        border: "rgba(135, 80, 166, 0.2)",
        
        // Tus colores personalizados
        primary: "#8750A6",
        secondary: "#5e7f73",
        "custom-amethist": "#8D47A5",
        "neon-blue": "#3b82f6",
        
        // Colores de soporte para inputs y tarjetas
        input: "rgba(255, 255, 255, 0.05)",
        card: "rgba(30, 30, 40, 0.6)",
        muted: {
          DEFAULT: "rgba(60, 60, 75, 0.3)",
          foreground: "#a0a0b0"
        }
      },
      borderRadius: {
        lg: "0.625rem",
      }
    },
  },
  plugins: [],
}