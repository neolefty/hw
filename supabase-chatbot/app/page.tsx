import Image from "next/image";
import Link from "next/link"

export default function Home() {
  return (
    <div className="font-sans grid grid-rows-[20px_1fr_20px] items-center justify-items-center min-h-screen p-8 pb-20 gap-16 sm:p-20">
      <main className="flex flex-col gap-8 row-start-2 items-center sm:items-start">
        <h1 className="text-4xl">Supabase Chatbot</h1>
        <p className="text-xl">Based on <Link target="_blank" href="https://www.youtube.com/watch?v=Tt45NrVIBn8">Build a Chatbot with Next.js, LangChain, OpenAI, and Supabase Vector</Link></p>
      </main>
      <footer className="row-start-3 flex gap-6 flex-wrap items-center justify-center text-xl">
        <a
          className="flex items-center gap-2 hover:underline hover:underline-offset-4"
          href="https://neolefty.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          <Image
            aria-hidden
            src="/megaphone.svg"
            alt="Megaphone Icon"
            width={36}
            height={36}
          />
          blog
        </a>
        <a
          className="flex items-center gap-2 hover:underline hover:underline-offset-4"
          href="https://github.com/neolefty/hw/tree/master/supabase-chatbot"
          target="_blank"
          rel="noopener noreferrer"
        >
          <Image
            aria-hidden
            src="/github-logo.svg"
            alt="Github Icon"
            width={28}
            height={28}
          />
          source
        </a>
      </footer>
    </div>
  );
}
