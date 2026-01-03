import type { Metadata } from 'next'
import '../styles/globals.css'
import { LayoutWrapper } from '@/components/LayoutWrapper'

export const metadata: Metadata = {
  title: 'QuickFIX Dashboard',
  description: 'Real-time FIX protocol monitoring and control dashboard',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en" className="dark" suppressHydrationWarning>
      <head>
        <style>{`
          :root {
            --background: 0 0% 3.6%;
            --foreground: 0 0% 98.2%;
            --card: 0 0% 8.5%;
            --card-foreground: 0 0% 98.2%;
            --popover: 0 0% 8.5%;
            --popover-foreground: 0 0% 98.2%;
            --muted: 0 0% 14.9%;
            --muted-foreground: 0 0% 63.9%;
            --accent: 0 0% 45%;
            --accent-foreground: 0 0% 9%;
            --destructive: 0 0% 55%;
            --destructive-foreground: 0 0% 9%;
            --border: 0 0% 14.9%;
            --input: 0 0% 14.9%;
            --primary: 0 0% 98.2%;
            --primary-foreground: 0 0% 9%;
            --secondary: 0 0% 14.9%;
            --secondary-foreground: 0 0% 98.2%;
            --ring: 0 0% 83.1%;
            --radius: 0.5rem;
          }
        `}</style>
      </head>
      <body className="bg-background text-foreground">
        <LayoutWrapper>
          {children}
        </LayoutWrapper>
      </body>
    </html>
  )
}
