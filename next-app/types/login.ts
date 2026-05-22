import { Dispatch, SetStateAction } from "react";

export interface FormDataProps {
  loginEmail: string;
  loginPassword: string;
}

export interface LoginFormData {
  loginEmail: string;
  loginPassword: string;
  loginSubmit: string;
}

export interface LoginProps {
  formData: FormDataProps;
  setFormData: Dispatch<SetStateAction<FormDataProps>>;
}
