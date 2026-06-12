import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from "react-router";
import App from '@/homes/App.jsx'
import { AuthProvider } from '@hooks/AuthContext.jsx'
import { Provider } from 'react-redux'
import store from '@stores'

createRoot(document.getElementById('root')).render(
  // <StrictMode>
    <BrowserRouter>
      <Provider store={store}>
				<AuthProvider>
					<App />
				</AuthProvider>
			</Provider>
    </BrowserRouter>
  // </StrictMode>,
)
