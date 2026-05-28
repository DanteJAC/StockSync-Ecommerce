import { createVuetify } from 'vuetify'
import 'vuetify/styles'
import '@mdi/font/css/materialdesignicons.css'

const lightTheme = {
  dark: false,
  colors: {
    background: '#DCE0FF',
    surface: '#FFFFFF',

    primary: '#94B0DA',
    secondary: '#57886C',
    accent: '#81A684',

    error: '#EF4444',
    info: '#94B0DA',
    success: '#81A684',
    warning: '#F59E0B',
  },
}

const darkTheme = {
  dark: true,
  colors: {
    background: '#020300',
    surface: '#111111',

    primary: '#81A684',
    secondary: '#94B0DA',
    accent: '#57886C',

    error: '#F87171',
    info: '#94B0DA',
    success: '#81A684',
    warning: '#FBBF24',
  },
}

export default createVuetify({
  theme: {
    defaultTheme: 'light',
    themes: {
      light: lightTheme,
      dark: darkTheme,
    },
  },
  defaults: {
    VBtn: {
      rounded: 'lg',
    },
    VCard: {
      rounded: 'lg',
    },
    VTextField: {
      variant: 'outlined',
      density: 'comfortable',
    },
    VSelect: {
      variant: 'outlined',
      density: 'comfortable',
    },
    VTextarea: {
      variant: 'outlined',
      density: 'comfortable',
    },
  },
})
