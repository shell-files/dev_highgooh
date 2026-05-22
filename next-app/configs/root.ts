import type { Metadata } from "next";

const SITE_NAME : string = "HG";

export const BASE_SITE_URL = "http://myapp.com";

export const defaultMetadata: Metadata = {
  title: {
    template: `%s | ${SITE_NAME}`,
    default: `${SITE_NAME} - 환영합니다`,
  },
  description: "A Next.js application",
  metadataBase: new URL(BASE_SITE_URL),
  openGraph: {
    type: "website",
    locale: "ko_KR",
    url: BASE_SITE_URL,
    siteName: SITE_NAME,
    images: [
      {
        url: "/icon.png",
        width: 1200,
        height: 630,
        alt: "HG 메인 이미지",
      },
    ],
  },
};

interface GenerateMetadataProps {
  title: string;
  description?: string;
  asPath?: string;
  ogImage?: string;
}

export function getCustomMetadata({ title, description, asPath, ogImage }: GenerateMetadataProps): Metadata {
  return {
    ...defaultMetadata,
    title: title,
    description: description || defaultMetadata.description,
    openGraph: {
      ...defaultMetadata.openGraph,
      title: `${title} | ${SITE_NAME}`,
      description: (description || defaultMetadata.description) ?? undefined,
      url: asPath ? `${BASE_SITE_URL}${asPath}` : BASE_SITE_URL,
      images: ogImage ? [{ url: ogImage }] : defaultMetadata.openGraph?.images,
    },
  };
}