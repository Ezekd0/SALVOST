import { Construction } from 'lucide-react';

export default function Placeholder({ title }: { title: string }) {
  return (
    <div className="flex flex-col items-center justify-center h-full min-h-[60vh]">
      <Construction className="h-16 w-16 text-teal opacity-50 mb-4" />
      <h1 className="text-2xl font-bold text-text-light">{title}</h1>
      <p className="text-text-muted mt-2 text-center max-w-md">
        This module is currently under development or not included in the minimum viable product (MVP) scope for the current demo.
      </p>
    </div>
  );
}
