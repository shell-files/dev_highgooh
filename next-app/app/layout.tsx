import { RootType } from "@/types";
import { defaultMetadata } from "@/configs";
import "@styles/globals.css";

export const metadata = defaultMetadata;

const RootLayout = ({ children, }: RootType) => {
  return (
    <html lang="ko">
      <body>{children}</body>
    </html>
  );
}

export default RootLayout;