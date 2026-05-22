"use client";

import {  useState, FormEventHandler } from "react";
import { useRouter } from "next/navigation";
import { FormDataProps, LoginFormData } from "@/types";

export const Login = () => {
  const router = useRouter();
  const [formData, setFormData] = useState<FormDataProps>({loginEmail: "", loginPassword: ""});
  const [errors, setErrors] = useState<LoginFormData>({loginEmail: "",loginPassword: "",loginSubmit: ""});
  const [loading, setLoading] = useState<boolean>();

  const validate = (name: string, value: string) => {
    let msg = "";
    if (!value.trim()) {
      msg = name.toLowerCase().includes("email") ? "이메일을 입력해 주세요." : "비밀번호를 입력해 주세요.";
    } else if (name.toLowerCase().includes("email") && !/\S+@\S+\.\S+/.test(value)) {
      msg = "올바른 이메일 주소를 입력해 주세요.";
    }
    setErrors(prev => ({ ...prev, [name]: msg }));
    return msg;
  };

  const handleLogin: FormEventHandler<HTMLFormElement> = async (e) => {
    e.preventDefault();
    // 1. 유효성 검사
    if (validate("loginEmail", formData.loginEmail) || validate("loginPassword", formData.loginPassword)) return;

    try {

    } catch (error) {
      setErrors(p => ({ ...p, loginSubmit: "이메일 또는 비밀번호가 일치하지 않습니다." }));
    } finally { setLoading(false); }
  }

  return (
    <div className="login-card active" id="login-section">
      <div className="header-nav"><span className="back-btn" onClick={() => router.push("/")}>←</span></div>
      <div className="login-logo-mark">ESG DATA PLATFORM</div>
      <h1>Login</h1>
      <form className="input-group" onSubmit={handleLogin}>
        <div className="input-wrapper">
          <input
            type="email"
            name="loginEmail"
            placeholder="이메일을 입력해주세요"
            className={errors["loginEmail"] ? "input-error" : ""}
            value={formData.loginEmail}
            autoComplete="off"
            onChange={e => {
              setFormData(p => ({ ...p, loginEmail: e.target.value }));
              setErrors(p => ({ ...p, loginEmail: "" }));
            }}
            onBlur={e => validate("loginEmail", e.target.value)}
          />
          {errors["loginEmail"] && <p className="error-text">{errors["loginEmail"]}</p>}
        </div>
        <div className="input-wrapper">
          <input
            type="password"
            name="loginPassword"
            placeholder="비밀번호를 입력해주세요"
            className={errors["loginPassword"] ? "input-error" : ""}
            value={formData.loginPassword}
            autoComplete="password"
            onChange={e => {
              setFormData(p => ({ ...p, loginPassword: e.target.value }));
              setErrors(p => ({ ...p, loginPassword: "" }));
            }}
            onBlur={e => validate("loginPassword", e.target.value)}
          />
          {errors["loginPassword"] && <p className="error-text">{errors["loginPassword"]}</p>}
        </div>
        <button className="login-action-button" type="submit" disabled={loading}>
          {loading ? <span className="button-spinner" /> : "로그인"}
        </button>
        {errors.loginSubmit && <p className="error-text submit-error">{errors.loginSubmit}</p>}
      </form>
    </div>
  );
}

