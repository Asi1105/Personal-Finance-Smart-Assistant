import { Outlet } from 'react-router'
import { Sidebar } from './Sidebar'
import { MobileSidebar } from './MobileSidebar'
import { Header } from './Header'

export function AppLayout() {
  return (
    <div className="flex h-screen bg-gradient-to-br from-gray-50 via-white to-violet-50/30 dark:from-gray-900 dark:via-gray-800 dark:to-violet-900/30 overflow-hidden">
      {/* Animated background elements */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <div className="absolute top-20 left-1/4 w-32 h-32 bg-gradient-to-br from-violet-200/30 to-purple-200/30 rounded-full mix-blend-multiply filter blur-xl animate-float"></div>
        <div className="absolute top-60 right-1/4 w-40 h-40 bg-gradient-to-br from-emerald-200/30 to-cyan-200/30 rounded-full mix-blend-multiply filter blur-xl animate-float animation-delay-2000"></div>
        <div className="absolute bottom-40 left-1/3 w-36 h-36 bg-gradient-to-br from-pink-200/30 to-rose-200/30 rounded-full mix-blend-multiply filter blur-xl animate-float animation-delay-4000"></div>
      </div>

      <Sidebar />
      <MobileSidebar />
      <div className="flex-1 flex flex-col overflow-hidden relative">
        <Header />
        <main className="flex-1 overflow-y-auto relative">
          <div className="container mx-auto px-4 sm:px-6 lg:px-8 py-4 sm:py-6 lg:py-8 relative z-10">
            <div className="backdrop-blur-sm">
              <Outlet />
            </div>
          </div>
        </main>
      </div>
    </div>
  )
}