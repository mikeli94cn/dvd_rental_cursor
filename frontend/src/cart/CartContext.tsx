import { createContext, useContext, useMemo, useState, type ReactNode } from 'react'
import type { CartItem, Movie } from '../types/api'

interface CartContextValue {
  items: CartItem[]
  add: (movie: Movie, dvdCopyId: number, barcode: string) => void
  remove: (dvdCopyId: number) => void
  clear: () => void
}

const CartContext = createContext<CartContextValue | undefined>(undefined)

export function CartProvider({ children }: { children: ReactNode }) {
  const [items, setItems] = useState<CartItem[]>([])

  const value = useMemo(
    () => ({
      items,
      add: (movie: Movie, dvdCopyId: number, barcode: string) => {
        setItems((prev) => {
          if (prev.some((i) => i.dvdCopyId === dvdCopyId)) {
            return prev
          }
          return [...prev, { movie, dvdCopyId, barcode }]
        })
      },
      remove: (dvdCopyId: number) => setItems((prev) => prev.filter((i) => i.dvdCopyId !== dvdCopyId)),
      clear: () => setItems([]),
    }),
    [items],
  )

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>
}

export function useCart() {
  const ctx = useContext(CartContext)
  if (!ctx) {
    throw new Error('useCart must be used within CartProvider')
  }
  return ctx
}
