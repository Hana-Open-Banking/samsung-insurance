import type React from "react"
import type { Metadata } from "next"
import "./globals.css"
import Header from "@/components/header"

export const metadata: Metadata = {
  title: "삼성화재",
  description: "삼성화재 공식 웹사이트",
  generator: "v0.dev",
}

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode
}>) {
  return (
    <html lang="ko">
      <body>
        <Header />
        {children}
      </body>
    </html>
  )
}
