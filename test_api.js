async function test() {
    try {
        const loginRes = await fetch('http://localhost:8080/api/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                email: 'jacobo.bustos.22@gmail.com',
                password: 'password'
            })
        });
        
        if (!loginRes.ok) {
            console.error("Login failed:", await loginRes.text());
            return;
        }
        
        const loginData = await loginRes.json();
        const token = loginData.token;

        const usersRes = await fetch('http://localhost:8080/api/users', {
            headers: { Authorization: `Bearer ${token}` }
        });
        
        const usersData = await usersRes.json();
        console.log("Users JSON:");
        console.log(JSON.stringify(usersData, null, 2));
    } catch (e) {
        console.error("Error:", e.message);
    }
}

test();
