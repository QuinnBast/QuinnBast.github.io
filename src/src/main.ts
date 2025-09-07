import './assets/main.css'

import { createApp } from 'vue'
import PrimeVue from 'primevue/config';
import App from './App.vue'
import router from './router'
import Lara from '@primeuix/themes/lara';
import {definePreset} from "@primeuix/themes";

const stylePreset = definePreset(Lara, {
    semantic: {
        primary: {
            50: '{teal.50}',
            100: '{teal.100}',
            200: '{teal.200}',
            300: '{teal.300}',
            400: '{teal.400}',
            500: '{teal.500}',
            600: '{teal.600}',
            700: '{teal.700}',
            800: '{teal.800}',
            900: '{teal.900}',
            950: '{teal.950}'
        },
        colorScheme: {
            dark: {
                surface: {
                    0: '#ffffff',
                    50: '{zinc.50}',
                    100: '{zinc.100}',
                    200: '{zinc.200}',
                    300: '{zinc.300}',
                    400: '{zinc.400}',
                    500: '{zinc.500}',
                    600: '{zinc.600}',
                    700: '{zinc.700}',
                    800: '{zinc.800}',
                    900: '{zinc.900}',
                    950: '{zinc.950}'
                }
            }
        }
    }
});

const app = createApp(App);
app.config.warnHandler = () => {}; // Disables all warning logs
app.use(PrimeVue, {
    theme: {
        preset: stylePreset,
        options: {
            darkModeSelector: '.app-dark',
            cssLayer: {
                name: 'primevue',
                order: 'app-styles, primevue'
            }
        }
    }
});

app.use(router)
app.use(PrimeVue, {
    theme: {
        preset: stylePreset,
        options: {
            darkModeSelector: '.app-dark'
        }
    }
});

app.mount('#app')
