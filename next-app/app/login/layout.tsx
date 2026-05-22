import { RootType } from "@/types";
import { LoginBackground, LoginVisualPanel } from "@/components";
import { getCustomMetadata } from "@/configs";

import "@styles/logins.css";
import "@styles/LoginBackground.css";

export const metadata = getCustomMetadata({
  title: "Login",
  description: "Please login to your account",
  asPath: "/login",
});

const LoginLayout = ({ children, }: RootType) => {
  return (
    <div id="login">
      <LoginBackground>
        <div className="login-combined-card">
          <LoginVisualPanel />
          <section className="login-form-panel">
            <div className="login-card-viewport">
              {children}
            </div>
          </section>
        </div>
      </LoginBackground>
    </div>
  );
}

export default LoginLayout;