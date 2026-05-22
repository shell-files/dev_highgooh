import { prisma } from "@/lib/prisma";

export default async function Home() {
    const users = await prisma.post.findMany();
    return (
        <div>
            <h1>Users</h1>
            {users.map((user) => (
                <div key={user.id}>{ JSON.stringify(user) }</div>
            ))}
        </div>
    );
}