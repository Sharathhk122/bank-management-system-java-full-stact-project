<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>NextGen Finance Hub</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/three.js/r128/three.min.js"></script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap');
        
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Poppins', sans-serif;
            scroll-behavior: smooth;
        }
        
        body {
            background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
            color: #e2e8f0;
            overflow-x: hidden;
        }
        
        .hero {
            min-height: 100vh;
            display: flex;
            align-items: center;
            position: relative;
            overflow: hidden;
        }
        
        .canvas-container {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            z-index: 1;
        }
        
        .hero-content {
            position: relative;
            z-index: 2;
            max-width: 1200px;
            margin: 0 auto;
            padding: 2rem;
            text-align: center;
        }
        
        .logo {
            font-size: 4rem;
            margin-bottom: 1.5rem;
            color: #38bdf8;
            text-shadow: 0 0 20px rgba(56, 189, 248, 0.5);
            animation: pulse 2s infinite;
        }
        
        @keyframes pulse {
            0% { transform: scale(1); }
            50% { transform: scale(1.05); }
            100% { transform: scale(1); }
        }
        
        .title {
            font-size: 3.5rem;
            font-weight: 700;
            margin-bottom: 1rem;
            background: linear-gradient(90deg, #38bdf8, #818cf8, #f472b6);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            animation: gradientShift 5s ease infinite;
        }
        
        @keyframes gradientShift {
            0% { background-position: 0% 50%; }
            50% { background-position: 100% 50%; }
            100% { background-position: 0% 50%; }
        }
        
        .subtitle {
            font-size: 1.5rem;
            margin-bottom: 2rem;
            opacity: 0.9;
        }
        
        .btn {
            display: inline-block;
            padding: 1rem 2rem;
            background: linear-gradient(90deg, #38bdf8, #818cf8);
            color: white;
            border-radius: 50px;
            text-decoration: none;
            font-weight: 600;
            margin: 0.5rem;
            transition: all 0.3s ease;
            box-shadow: 0 4px 15px rgba(56, 189, 248, 0.3);
        }
        
        .btn:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 25px rgba(56, 189, 248, 0.5);
        }
        
        .section {
            padding: 5rem 2rem;
            max-width: 1200px;
            margin: 0 auto;
        }
        
        .section-title {
            font-size: 2.5rem;
            text-align: center;
            margin-bottom: 3rem;
            position: relative;
            padding-bottom: 1rem;
        }
        
        .section-title::after {
            content: '';
            position: absolute;
            bottom: 0;
            left: 50%;
            transform: translateX(-50%);
            width: 100px;
            height: 4px;
            background: linear-gradient(90deg, #38bdf8, #818cf8);
            border-radius: 2px;
        }
        
        .tech-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 2rem;
            margin-top: 3rem;
        }
        
        .tech-card {
            background: rgba(30, 41, 59, 0.6);
            border-radius: 15px;
            padding: 2rem;
            text-align: center;
            transition: all 0.3s ease;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(56, 189, 248, 0.2);
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
        }
        
        .tech-card:hover {
            transform: translateY(-10px);
            box-shadow: 0 15px 35px rgba(56, 189, 248, 0.2);
            border-color: rgba(56, 189, 248, 0.4);
        }
        
        .tech-icon {
            font-size: 3rem;
            margin-bottom: 1rem;
            color: #38bdf8;
        }
        
        .feature-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 2rem;
            margin-top: 3rem;
        }
        
        .feature-card {
            background: rgba(30, 41, 59, 0.6);
            border-radius: 15px;
            padding: 2rem;
            transition: all 0.3s ease;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(56, 189, 248, 0.2);
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
        }
        
        .feature-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 15px 35px rgba(56, 189, 248, 0.2);
            border-color: rgba(56, 189, 248, 0.4);
        }
        
        .feature-icon {
            font-size: 2.5rem;
            margin-bottom: 1rem;
            color: #38bdf8;
        }
        
        .screenshot-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 2rem;
            margin-top: 3rem;
        }
        
        .screenshot {
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.3);
            transition: all 0.3s ease;
            position: relative;
            height: 200px;
            background: #475569;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #94a3b8;
        }
        
        .screenshot:hover {
            transform: translateY(-5px) scale(1.02);
            box-shadow: 0 15px 35px rgba(56, 189, 248, 0.2);
        }
        
        .live-demo {
            text-align: center;
            padding: 5rem 2rem;
            background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
            border-radius: 20px;
            margin: 4rem auto;
            max-width: 1200px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
        }
        
        .footer {
            text-align: center;
            padding: 3rem 2rem;
            margin-top: 5rem;
            background: rgba(15, 23, 42, 0.8);
            border-top: 1px solid rgba(56, 189, 248, 0.2);
        }
        
        .floating {
            animation: floating 3s ease-in-out infinite;
        }
        
        @keyframes floating {
            0% { transform: translate(0, 0px); }
            50% { transform: translate(0, 15px); }
            100% { transform: translate(0, -0px); }
        }
        
        .badge {
            display: inline-block;
            padding: 0.35rem 0.75rem;
            border-radius: 50px;
            font-size: 0.875rem;
            font-weight: 600;
            margin: 0.25rem;
        }
        
        .badge-frontend {
            background: rgba(56, 189, 248, 0.2);
            color: #38bdf8;
        }
        
        .badge-backend {
            background: rgba(248, 113, 113, 0.2);
            color: #f87171;
        }
        
        .badge-database {
            background: rgba(74, 222, 128, 0.2);
            color: #4ade80;
        }
        
        .typewriter {
            overflow: hidden;
            border-right: 0.15em solid #38bdf8;
            white-space: nowrap;
            margin: 0 auto;
            letter-spacing: 0.15em;
            animation: typing 3.5s steps(40, end), blink-caret 0.75s step-end infinite;
        }
        
        @keyframes typing {
            from { width: 0 }
            to { width: 100% }
        }
        
        @keyframes blink-caret {
            from, to { border-color: transparent }
            50% { border-color: #38bdf8 }
        }
        
        @media (max-width: 768px) {
            .title {
                font-size: 2.5rem;
            }
            
            .subtitle {
                font-size: 1.2rem;
            }
            
            .tech-grid, .feature-grid, .screenshot-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <!-- Hero Section with 3D Animation -->
    <section class="hero">
        <div class="canvas-container" id="canvas-container"></div>
        <div class="hero-content">
            <div class="logo floating">
                <i class="fas fa-coins"></i>
            </div>
            <h1 class="title">NextGen Finance Hub</h1>
            <p class="subtitle typewriter">Revolutionary Financial Management Platform</p>
            <div class="mt-8">
                <a href="#live-demo" class="btn"><i class="fas fa-play-circle mr-2"></i> Live Demo</a>
                <a href="#features" class="btn" style="background: linear-gradient(90deg, #f472b6, #818cf8);"><i class="fas fa-star mr-2"></i> Features</a>
            </div>
        </div>
    </section>

    <!-- Technologies Section -->
    <section class="section" id="technologies">
        <h2 class="section-title">Technology Stack</h2>
        <div class="tech-grid">
            <div class="tech-card">
                <div class="tech-icon">
                    <i class="fab fa-react"></i>
                </div>
                <h3 class="text-xl font-bold mb-2">Frontend</h3>
                <p>React, TailwindCSS, 3D Animations</p>
                <span class="badge badge-frontend">Modern UI</span>
            </div>
            
            <div class="tech-card">
                <div class="tech-icon">
                    <i class="fas fa-server"></i>
                </div>
                <h3 class="text-xl font-bold mb-2">Backend</h3>
                <p>Java Spring Boot, JPA, Hibernate, Spring Security</p>
                <span class="badge badge-backend">Maven Project</span>
            </div>
            
            <div class="tech-card">
                <div class="tech-icon">
                    <i class="fas fa-database"></i>
                </div>
                <h3 class="text-xl font-bold mb-2">Database</h3>
                <p>SQL Database for secure data storage</p>
                <span class="badge badge-database">Relational DB</span>
            </div>
        </div>
    </section>

    <!-- Features Section -->
    <section class="section" id="features">
        <h2 class="section-title">Key Features</h2>
        <div class="feature-grid">
            <div class="feature-card">
                <div class="feature-icon">
                    <i class="fas fa-id-card"></i>
                </div>
                <h3 class="text-xl font-bold mb-2">KYC Verification</h3>
                <p>Complete Know Your Customer process with document verification and validation.</p>
            </div>
            
            <div class="feature-card">
                <div class="feature-icon">
                    <i class="fas fa-university"></i>
                </div>
                <h3 class="text-xl font-bold mb-2">Account Management</h3>
                <p>Create and manage multiple accounts with ease and track all transactions.</p>
            </div>
            
            <div class="feature-card">
                <div class="feature-icon">
                    <i class="fas fa-hand-holding-usd"></i>
                </div>
                <h3 class="text-xl font-bold mb-2">Loan Processing</h3>
                <p>Apply for loans, check eligibility, and get quick approval with minimal documentation.</p>
            </div>
            
            <div class="feature-card">
                <div class="feature-icon">
                    <i class="fas fa-credit-card"></i>
                </div>
                <h3 class="text-xl font-bold mb-2">EMI Payments</h3>
                <p>Easy EMI payment system with flexible tenure options and reminders.</p>
            </div>
            
            <div class="feature-card">
                <div class="feature-icon">
                    <i class="fas fa-sms"></i>
                </div>
                <h3 class="text-xl font-bold mb-2">Real-time OTP Verification</h3>
                <p>8-digit OTP verification for secure transactions and account activities.</p>
            </div>
            
            <div class="feature-card">
                <div class="feature-icon">
                    <i class="fas fa-exchange-alt"></i>
                </div>
                <h3 class="text-xl font-bold mb-2">Transaction Management</h3>
                <p>Seamless money transfers between accounts with complete transaction history.</p>
            </div>
            
            <div class="feature-card">
                <div class="feature-icon">
                    <i class="fas fa-user-shield"></i>
                </div>
                <h3 class="text-xl font-bold mb-2">Admin Dashboard</h3>
                <p>Complete admin panel to manage users, approvals, and generate reports.</p>
            </div>
            
            <div class="feature-card">
                <div class="feature-icon">
                    <i class="fas fa-file-download"></i>
                </div>
                <h3 class="text-xl font-bold mb-2">Reports & Analytics</h3>
                <p>Downloadable PDF and Excel reports for all data including KYC, accounts, loans, and transactions.</p>
            </div>
        </div>
    </section>

    <!-- Screenshots Section -->
    <section class="section" id="screenshots">
        <h2 class="section-title">Application Screenshots</h2>
        <div class="screenshot-grid">
            <div class="screenshot">Login Page</div>
            <div class="screenshot">Dashboard</div>
            <div class="screenshot">KYC Verification</div>
            <div class="screenshot">Account Overview</div>
            <div class="screenshot">Loan Application</div>
            <div class="screenshot">EMI Payment</div>
            <div class="screenshot">OTP Verification</div>
            <div class="screenshot">Transaction History</div>
            <div class="screenshot">Admin Dashboard</div>
            <div class="screenshot">User Management</div>
            <div class="screenshot">Report Generation</div>
            <div class="screenshot">Profile Settings</div>
            <div class="screenshot">Notifications</div>
            <div class="screenshot">Account Statements</div>
            <div class="screenshot">Loan Calculator</div>
            <div class="screenshot">Payment Gateway</div>
            <div class="screenshot">Security Settings</div>
            <div class="screenshot">Help & Support</div>
            <div class="screenshot">Mobile Responsive View</div>
            <div class="screenshot">Dark/Light Mode</div>
        </div>
    </section>

    <!-- Live Demo Section -->
    <section class="live-demo" id="live-demo">
        <h2 class="section-title">Live Demo</h2>
        <p class="text-xl mb-8">Experience the NextGen Finance Hub platform live</p>
        <a href="https://nextgen-finance-hub.onrender.com/" target="_blank" class="btn"><i class="fas fa-external-link-alt mr-2"></i> Visit Live Website</a>
        <div class="mt-12">
            <h3 class="text-2xl font-bold mb-4">Platform Access</h3>
            <div class="flex flex-wrap justify-center">
                <div class="bg-slate-800 rounded-lg p-4 m-2 w-64">
                    <h4 class="font-bold text-lg mb-2">Admin Access</h4>
                    <p>Username: admin@nextgen.com</p>
                    <p>Password: admin123</p>
                </div>
                <div class="bg-slate-800 rounded-lg p-4 m-2 w-64">
                    <h4 class="font-bold text-lg mb-2">User Access</h4>
                    <p>Username: user@nextgen.com</p>
                    <p>Password: user123</p>
                </div>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <footer class="footer">
        <p>© 2023 NextGen Finance Hub. All rights reserved.</p>
        <div class="mt-4">
            <a href="#" class="text-sky-400 mx-2"><i class="fab fa-github"></i></a>
            <a href="#" class="text-sky-400 mx-2"><i class="fab fa-linkedin"></i></a>
            <a href="#" class="text-sky-400 mx-2"><i class="fab fa-twitter"></i></a>
        </div>
    </footer>

    <!-- Three.js 3D Animation -->
    <script>
        // Three.js initialization
        let scene, camera, renderer, particles;
        
        function init() {
            // Scene setup
            scene = new THREE.Scene();
            camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
            camera.position.z = 5;
            
            // Renderer setup
            renderer = new THREE.WebGLRenderer({ alpha: true });
            renderer.setSize(window.innerWidth, window.innerHeight);
            renderer.setClearColor(0x000000, 0);
            document.getElementById('canvas-container').appendChild(renderer.domElement);
            
            // Create particles
            const geometry = new THREE.BufferGeometry();
            const vertices = [];
            
            for (let i = 0; i < 1000; i++) {
                const x = (Math.random() - 0.5) * 20;
                const y = (Math.random() - 0.5) * 20;
                const z = (Math.random() - 0.5) * 20;
                vertices.push(x, y, z);
            }
            
            geometry.setAttribute('position', new THREE.Float32BufferAttribute(vertices, 3));
            
            const material = new THREE.PointsMaterial({
                color: 0x38bdf8,
                size: 0.05,
                transparent: true,
                blending: THREE.AdditiveBlending
            });
            
            particles = new THREE.Points(geometry, material);
            scene.add(particles);
            
            // Handle window resize
            window.addEventListener('resize', onWindowResize, false);
            
            // Start animation
            animate();
        }
        
        function onWindowResize() {
            camera.aspect = window.innerWidth / window.innerHeight;
            camera.updateProjectionMatrix();
            renderer.setSize(window.innerWidth, window.innerHeight);
        }
        
        function animate() {
            requestAnimationFrame(animate);
            
            particles.rotation.x += 0.001;
            particles.rotation.y += 0.002;
            
            const positions = particles.geometry.attributes.position.array;
            for (let i = 0; i < positions.length; i += 3) {
                positions[i] += (Math.random() - 0.5) * 0.01;
                positions[i + 1] += (Math.random() - 0.5) * 0.01;
                positions[i + 2] += (Math.random() - 0.5) * 0.01;
            }
            particles.geometry.attributes.position.needsUpdate = true;
            
            renderer.render(scene, camera);
        }
        
        // Initialize the 3D animation
        init();
    </script>
</body>
</html>
