import * as React from "react"

export interface StatusBadgeProps extends React.HTMLAttributes<HTMLDivElement> {
  status: "connected" | "disconnected" | "connecting"
}

const StatusBadge = React.forwardRef<HTMLDivElement, StatusBadgeProps>(
  ({ status, className = "", ...props }, ref) => {
    const statusStyles = {
      connected: "bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200",
      disconnected: "bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200",
      connecting: "bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200",
    }

    const statusText = {
      connected: "Connected",
      disconnected: "Disconnected",
      connecting: "Connecting...",
    }

    const dotColor = {
      connected: "bg-green-500",
      disconnected: "bg-red-500",
      connecting: "bg-yellow-500 animate-pulse",
    }

    return (
      <div
        ref={ref}
        className={`inline-flex items-center gap-2 px-3 py-1 rounded-full text-sm font-medium ${statusStyles[status]} ${className}`}
        {...props}
      >
        <div className={`w-2 h-2 rounded-full ${dotColor[status]}`} />
        {statusText[status]}
      </div>
    )
  }
)
StatusBadge.displayName = "StatusBadge"

export { StatusBadge }
