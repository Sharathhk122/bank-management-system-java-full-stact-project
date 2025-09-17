// CustomerDashboard.jsx
import React, { useState, useEffect, useRef } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { 
  Card, 
  Progress, 
  Tag, 
  Alert, 
  Skeleton, 
  Row, 
  Col, 
  Statistic, 
  Tabs, 
  List, 
  Badge,
  Tooltip,
  Divider,
  Button,
  Avatar,
  notification,
  Space,
  Typography
} from 'antd';
import { 
  BankOutlined, 
  DollarOutlined, 
  CreditCardOutlined, 
  UserOutlined,
  SendOutlined,
  TeamOutlined,
  CalculatorOutlined,
  EyeOutlined,
  SafetyCertificateOutlined,
  RocketOutlined,
  PieChartOutlined,
  ArrowUpOutlined,
  ArrowDownOutlined,
  HistoryOutlined,
  FileTextOutlined,
  BarChartOutlined,
  WalletOutlined,
  SettingOutlined,
  BellOutlined,
  CalendarOutlined,
  TransactionOutlined,
  RiseOutlined,
  FallOutlined,
  IdcardOutlined,
  SecurityScanOutlined,
  MoreOutlined,
  PlusOutlined,
  ArrowRightOutlined,
  DashboardOutlined,
  StarOutlined,
  TrophyOutlined,
  GiftOutlined,
  CrownOutlined,
  SyncOutlined,
  ThunderboltOutlined,
  GlobalOutlined,
  RadarChartOutlined,
  ClusterOutlined,
  FundOutlined,
  ApiOutlined,
  PartitionOutlined,
  NodeIndexOutlined,
  GatewayOutlined,
  DeploymentUnitOutlined,
  CodeSandboxOutlined,
  BoxPlotOutlined,
  BlockOutlined
} from '@ant-design/icons';

const { Title, Text } = Typography;
const { TabPane } = Tabs;

const CustomerDashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [accounts, setAccounts] = useState([
    {
      accountNumber: '1234567890',
      accountName: 'Primary Savings Account',
      balance: 125000.75,
      status: 'ACTIVE'
    },
    {
      accountNumber: '0987654321',
      accountName: 'Current Account',
      balance: 45000.50,
      status: 'ACTIVE'
    }
  ]);
  const [loans, setLoans] = useState([
    {
      id: 1,
      productName: 'Personal Loan',
      loanAmount: 500000,
      outstandingBalance: 325000,
      status: 'DISBURSED',
      disbursementDate: '2023-06-15'
    },
    {
      id: 2,
      productName: 'Home Loan',
      loanAmount: 2500000,
      outstandingBalance: 2100000,
      status: 'DISBURSED',
      disbursementDate: '2022-11-20'
    }
  ]);
  const [kycStatus, setKycStatus] = useState({ status: 'APPROVED' });
  const [recentTransactions, setRecentTransactions] = useState([
    {
      id: 1,
      type: 'DEBIT',
      amount: 2500.00,
      description: 'Electricity Bill Payment',
      timestamp: '2023-10-15T14:30:00Z'
    },
    {
      id: 2,
      type: 'CREDIT',
      amount: 50000.00,
      description: 'Salary Credit',
      timestamp: '2023-10-05T09:15:00Z'
    },
    {
      id: 3,
      type: 'DEBIT',
      amount: 1500.00,
      description: 'Mobile Recharge',
      timestamp: '2023-10-03T16:45:00Z'
    },
    {
      id: 4,
      type: 'DEBIT',
      amount: 3500.00,
      description: 'Online Shopping',
      timestamp: '2023-10-01T19:20:00Z'
    },
    {
      id: 5,
      type: 'CREDIT',
      amount: 12000.00,
      description: 'Freelance Payment',
      timestamp: '2023-09-28T11:30:00Z'
    }
  ]);
  const [loading, setLoading] = useState(false);
  const [activeTab, setActiveTab] = useState('overview');
  const canvasRef = useRef(null);
  const headerTextRef = useRef(null);
  const titleTextRef = useRef(null);
  const particlesRef = useRef([]);
  const animationRef = useRef(null);
  const threeDContainerRef = useRef(null);
  const headerAnimationComplete = useRef(false);
  const titleAnimationComplete = useRef(false);

  useEffect(() => {
    // Typewriter effect for header
    if (headerTextRef.current && !headerAnimationComplete.current) {
      const text = "Welcome to your Quantum Banking Experience";
      headerTextRef.current.textContent = '';
      let i = 0;
      const typeWriter = () => {
        if (i < text.length && headerTextRef.current) {
          headerTextRef.current.textContent += text.charAt(i);
          i++;
          setTimeout(typeWriter, 80);
        } else {
          headerAnimationComplete.current = true;
        }
      };
      typeWriter();
    }

    // Typewriter effect for title
    if (titleTextRef.current && !titleAnimationComplete.current) {
      const text = "NextGen Finance Hub";
      titleTextRef.current.textContent = '';
      let i = 0;
      const typeWriterTitle = () => {
        if (i < text.length && titleTextRef.current) {
          titleTextRef.current.textContent += text.charAt(i);
          i++;
          setTimeout(typeWriterTitle, 100);
        } else if (titleTextRef.current && titleTextRef.current.parentNode) {
          titleAnimationComplete.current = true;
          // Add the crown icon after the text animation completes
          const crownIcon = document.createElement('span');
          crownIcon.className = 'ml-2 text-yellow-400 animate-spin-slow';
          crownIcon.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-6 h-6"><path d="M14.615 1.595a.75.75 0 01.359.852L12.982 9.75h7.268a.75.75 0 01.548 1.262l-10.5 11.25a.75.75 0 01-1.272-.71l1.992-7.302H3.75a.75.75 0 01-.548-1.262l10.5-11.25a.75.75 0 01.913-.143z" /></svg>';
          titleTextRef.current.parentNode.appendChild(crownIcon);
        }
      };
      typeWriterTitle();
    }

    // Initialize 3D background
    init3DBackground();

    return () => {
      if (animationRef.current) {
        cancelAnimationFrame(animationRef.current);
      }
    };
  }, []);

  const init3DBackground = () => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    
    const ctx = canvas.getContext('2d');
    const container = canvas.parentElement;
    
    // Set canvas size
    const resizeCanvas = () => {
      canvas.width = container.offsetWidth;
      canvas.height = container.offsetHeight;
    };
    
    resizeCanvas();
    window.addEventListener('resize', resizeCanvas);
    
    // Enhanced Particle system
    class Particle {
      constructor() {
        this.x = Math.random() * canvas.width;
        this.y = Math.random() * canvas.height;
        this.size = Math.random() * 3 + 1; // Reduced max size to prevent negative values
        this.speedX = Math.random() * 3 - 1.5;
        this.speedY = Math.random() * 3 - 1.5;
        this.color = `hsla(${Math.random() * 360}, 80%, 60%, ${Math.random() * 0.5 + 0.2})`;
        this.angle = Math.random() * Math.PI * 2;
        this.angleSpeed = Math.random() * 0.05;
        this.orbitRadius = Math.random() * 50 + 10;
        this.originalX = this.x;
        this.originalY = this.y;
        this.pulse = Math.random() * Math.PI * 2;
        this.pulseSpeed = Math.random() * 0.05 + 0.01;
        this.trail = [];
        this.maxTrail = 5;
        this.shape = Math.floor(Math.random() * 3); // 0: circle, 1: triangle, 2: square
      }
      
      update() {
        // Save current position to trail
        this.trail.push({x: this.x, y: this.y});
        if (this.trail.length > this.maxTrail) {
          this.trail.shift();
        }
        
        // Orbital motion with drift
        this.angle += this.angleSpeed;
        this.originalX += this.speedX * 0.1;
        this.originalY += this.speedY * 0.1;
        
        this.x = this.originalX + Math.cos(this.angle) * this.orbitRadius;
        this.y = this.originalY + Math.sin(this.angle) * this.orbitRadius;
        
        // Pulsing effect
        this.pulse += this.pulseSpeed;
        const pulseSize = Math.max(0.5, this.size + Math.sin(this.pulse) * 1.5); // Ensure positive size
        
        // Boundary check with wrap-around
        if (this.x > canvas.width + 50) this.x = -50;
        else if (this.x < -50) this.x = canvas.width + 50;
        
        if (this.y > canvas.height + 50) this.y = -50;
        else if (this.y < -50) this.y = canvas.height + 50;
        
        // Draw with pulsing effect
        this.draw(pulseSize);
        
        // Draw trail
        this.drawTrail();
      }
      
      draw(size) {
        // Ensure size is always positive
        const positiveSize = Math.max(0.5, size);
        
        ctx.beginPath();
        
        if (this.shape === 0) {
          // Circle
          ctx.arc(this.x, this.y, positiveSize, 0, Math.PI * 2);
        } else if (this.shape === 1) {
          // Triangle
          ctx.moveTo(this.x, this.y - positiveSize);
          ctx.lineTo(this.x - positiveSize, this.y + positiveSize);
          ctx.lineTo(this.x + positiveSize, this.y + positiveSize);
          ctx.closePath();
        } else {
          // Square
          ctx.rect(this.x - positiveSize, this.y - positiveSize, positiveSize * 2, positiveSize * 2);
        }
        
        ctx.fillStyle = this.color;
        ctx.fill();
        
        // Add glow effect
        ctx.shadowBlur = 20;
        ctx.shadowColor = this.color;
      }
      
      drawTrail() {
        for (let i = 0; i < this.trail.length; i++) {
          const point = this.trail[i];
          const alpha = i / this.trail.length * 0.3;
          
          ctx.beginPath();
          ctx.arc(point.x, point.y, this.size * 0.5, 0, Math.PI * 2);
          ctx.fillStyle = `hsla(${this.color.split(',')[0].split('(')[1]}, 80%, 60%, ${alpha})`;
          ctx.fill();
        }
      }
    }
    
    // Create particles
    particlesRef.current = [];
    const particleCount = Math.min(200, Math.floor((canvas.width * canvas.height) / 6000));
    
    for (let i = 0; i < particleCount; i++) {
      particlesRef.current.push(new Particle());
    }
    
    // Animation loop
    const animate = () => {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      
      // Draw gradient background with multiple color stops
      const gradient = ctx.createRadialGradient(
        canvas.width / 2,
        canvas.height / 2,
        0,
        canvas.width / 2,
        canvas.height / 2,
        Math.max(canvas.width, canvas.height) / 1.2
      );
      gradient.addColorStop(0, 'rgba(15, 23, 42, 0.9)');
      gradient.addColorStop(0.3, 'rgba(30, 41, 59, 0.8)');
      gradient.addColorStop(0.6, 'rgba(15, 23, 42, 0.7)');
      gradient.addColorStop(1, 'rgba(2, 6, 23, 0.9)');
      
      ctx.fillStyle = gradient;
      ctx.fillRect(0, 0, canvas.width, canvas.height);
      
      // Draw grid pattern in background
      ctx.strokeStyle = 'rgba(100, 100, 255, 0.05)';
      ctx.lineWidth = 0.5;
      
      const gridSize = 50;
      for (let x = 0; x < canvas.width; x += gridSize) {
        ctx.beginPath();
        ctx.moveTo(x, 0);
        ctx.lineTo(x, canvas.height);
        ctx.stroke();
      }
      
      for (let y = 0; y < canvas.height; y += gridSize) {
        ctx.beginPath();
        ctx.moveTo(0, y);
        ctx.lineTo(canvas.width, y);
        ctx.stroke();
      }
      
      // Update and draw particles
      particlesRef.current.forEach(particle => {
        particle.update();
      });
      
      // Connect particles with lines
      connectParticles();
      
      animationRef.current = requestAnimationFrame(animate);
    };
    
    // Connect particles with lines
    const connectParticles = () => {
      const maxDistance = 150;
      for (let a = 0; a < particlesRef.current.length; a++) {
        for (let b = a + 1; b < particlesRef.current.length; b++) {
          const dx = particlesRef.current[a].x - particlesRef.current[b].x;
          const dy = particlesRef.current[a].y - particlesRef.current[b].y;
          const distance = Math.sqrt(dx * dx + dy * dy);
          
          if (distance < maxDistance) {
            const opacity = 0.2 * (1 - distance/maxDistance);
            ctx.beginPath();
            ctx.strokeStyle = `hsla(${(particlesRef.current[a].color.split(',')[0].split('(')[1] + particlesRef.current[b].color.split(',')[0].split('(')[1]) / 2}, 70%, 60%, ${opacity})`;
            ctx.lineWidth = 0.7;
            ctx.moveTo(particlesRef.current[a].x, particlesRef.current[a].y);
            ctx.lineTo(particlesRef.current[b].x, particlesRef.current[b].y);
            ctx.stroke();
          }
        }
      }
    };
    
    // Start animation
    animate();
  };

  const getTotalBalance = () => {
    if (!accounts || accounts.length === 0) return 0;
    return accounts.reduce((total, account) => {
      const balance = parseFloat(account.balance) || 0;
      return total + balance;
    }, 0);
  };

  const getTotalLoanBalance = () => {
    if (!loans || loans.length === 0) return 0;
    return loans
      .filter(loan => loan.status === 'DISBURSED')
      .reduce((total, loan) => {
        const outstandingBalance = parseFloat(loan.outstandingBalance) || 0;
        return total + outstandingBalance;
      }, 0);
  };

  const getKYCStatusColor = (status) => {
    switch (status) {
      case 'APPROVED': return '#10b981';
      case 'REJECTED': return '#ef4444';
      case 'PENDING': return '#f59e0b';
      default: return '#6b7280';
    }
  };

  const getKYCStatusText = (status) => {
    switch (status) {
      case 'APPROVED': return 'Verified';
      case 'REJECTED': return 'Rejected';
      case 'PENDING': return 'Pending';
      default: return 'Not Submitted';
    }
  };

  const getLoanStatusColor = (status) => {
    switch (status) {
      case 'APPROVED': return '#3b82f6';
      case 'DISBURSED': return '#10b981';
      case 'PENDING': return '#f59e0b';
      case 'REJECTED': return '#ef4444';
      case 'CLOSED': return '#6b7280';
      default: return '#6b7280';
    }
  };

  const formatCurrency = (amount) => {
    if (amount === undefined || amount === null || isNaN(amount)) {
      return '₹0.00';
    }
    
    const numAmount = typeof amount === 'string' ? parseFloat(amount) : amount;
    
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(numAmount);
  };

  const getTransactionIcon = (type) => {
    return type === 'CREDIT' ? 
      <ArrowDownOutlined /> : 
      <ArrowUpOutlined />;
  };

  const handleViewAllAccounts = () => {
    navigate('/accounts');
  };

  const handleViewAllTransactions = () => {
    navigate('/transactions/history');
  };

  const handleViewAllLoans = () => {
    navigate('/loans');
  };

  const handleOpenNewAccount = () => {
    navigate('/accounts/new');
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-gray-900 to-gray-800 p-4 md:p-6">
        <div className="w-full mx-auto">
          <Skeleton active paragraph={{ rows: 10 }} />
        </div>
      </div>
    );
  }

  return (
    <div className="relative min-h-screen overflow-hidden bg-gradient-to-br from-gray-900 via-gray-900 to-gray-950 text-gray-100">
      {/* 3D Animated Background */}
      <canvas 
        ref={canvasRef} 
        className="fixed top-0 left-0 w-full h-full -z-10 opacity-80"
      />
      
      {/* Floating 3D Elements */}
      <div className="absolute top-1/4 left-1/4 w-64 h-64 rounded-full bg-cyan-500/10 blur-3xl animate-pulse-slow"></div>
      <div className="absolute bottom-1/3 right-1/4 w-96 h-96 rounded-full bg-purple-600/10 blur-3xl animate-pulse-medium"></div>
      <div className="absolute top-2/3 left-1/2 w-80 h-80 rounded-full bg-indigo-600/10 blur-3xl animate-pulse-fast"></div>
      <div className="absolute top-1/3 right-1/3 w-72 h-72 rounded-full bg-pink-500/10 blur-3xl animate-pulse-slower"></div>
      
      <div className="relative z-10 p-4 md:p-6 w-full">
        {/* Header Section */}
        <div className="mb-8 transform transition-all duration-700 hover:scale-[1.01]">
          <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 p-6 bg-gradient-to-r from-indigo-900/30 via-purple-900/30 to-cyan-900/30 rounded-2xl backdrop-blur-xl border border-indigo-500/30 shadow-2xl relative overflow-hidden">
            {/* Animated background elements */}
            <div className="absolute -top-20 -left-20 w-40 h-40 bg-cyan-400/10 rounded-full blur-xl animate-float"></div>
            <div className="absolute -bottom-20 -right-20 w-48 h-48 bg-purple-500/10 rounded-full blur-xl animate-float-reverse"></div>
            <div className="absolute top-10 right-1/4 w-32 h-32 bg-pink-500/10 rounded-full blur-xl animate-float-slow"></div>
            
            <div className="flex-1 z-10">
              <h1 className="dashboard-title text-3xl md:text-4xl font-bold mb-2 bg-clip-text text-transparent bg-gradient-to-r from-cyan-400 via-purple-400 to-pink-400 animate-text-shimmer">
                <span ref={titleTextRef}></span>
              </h1>
              <p ref={headerTextRef} className="text-indigo-200 mb-3 font-light text-sm md:text-base">
                {/* Text will be filled by typewriter effect */}
              </p>
              <div className="flex items-center text-sm text-cyan-300">
                <TrophyOutlined className="mr-2" />
                <span>Quantum Banking Member • Level 7</span>
                <span className="ml-3 px-2 py-1 bg-cyan-900/40 rounded-md text-xs border border-cyan-700/50 flex items-center">
                  <SyncOutlined spin className="mr-1" /> Live Sync Active
                </span>
              </div>
            </div>
            <div className="flex items-center space-x-3 z-10">
              <Tooltip title="Notifications" placement="bottom">
                <button className="p-2 rounded-full bg-gray-800/50 hover:bg-indigo-700/50 transition-all duration-300 transform hover:scale-110 border border-gray-700/50 shadow-lg hover:shadow-cyan-400/20">
                  <BellOutlined className="text-cyan-400" />
                  <Badge count={3} size="small" className="ml-1" />
                </button>
              </Tooltip>
              <Tooltip title="Financial Insights" placement="bottom">
                <button className="p-2 rounded-full bg-gray-800/50 hover:bg-indigo-700/50 transition-all duration-300 transform hover:scale-110 border border-gray-700/50 shadow-lg hover:shadow-green-400/20">
                  <BarChartOutlined className="text-green-400" />
                </button>
              </Tooltip>
              <Tooltip title="Settings" placement="bottom">
                <Link to="/settings">
                  <button className="p-2 rounded-full bg-gray-800/50 hover:bg-indigo-700/50 transition-all duration-300 transform hover:scale-110 border border-gray-700/50 shadow-lg hover:shadow-purple-400/20">
                    <SettingOutlined className="text-purple-400" />
                  </button>
                </Link>
              </Tooltip>
              <div className="flex items-center ml-2">
                <Avatar
                  size={44}
                  icon={<UserOutlined />}
                  className="border-2 border-cyan-500/50 shadow-lg shadow-cyan-500/20 animate-border-pulse"
                />
                <div className="ml-3">
                  <div className="font-medium text-cyan-100">{user?.name || 'User'}</div>
                  <div className="text-xs text-gray-400 flex items-center">
                    Quantum Account 
                    <ThunderboltOutlined className="text-yellow-400 ml-1" />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* KYC Status Alert */}
        {(!kycStatus || kycStatus.status !== 'APPROVED') && (
          <div className="mb-8 animate-fade-in-up">
            <div className="p-5 bg-gradient-to-r from-amber-900/30 to-orange-800/30 rounded-2xl backdrop-blur-md border border-amber-500/30 shadow-lg transform transition-all duration-500 hover:scale-[1.01] relative overflow-hidden">
              <div className="absolute -top-10 -right-10 w-28 h-28 bg-amber-400/10 rounded-full blur-xl"></div>
              <div className="flex items-start">
                <div className="flex-shrink-0 p-3 bg-amber-500/20 rounded-xl mr-4">
                  <SecurityScanOutlined className="text-2xl text-amber-400" />
                </div>
                <div className="flex-1">
                  <h3 className="text-lg font-semibold text-amber-200 mb-1">
                    Identity Verification: {getKYCStatusText(kycStatus?.status || 'NOT_SUBMITTED')}
                  </h3>
                  <p className="text-amber-100/80 mb-4">
                    {kycStatus?.status === 'PENDING'
                      ? 'Your verification documents are being processed. This typically takes 1-2 business days.'
                      : kycStatus?.status === 'REJECTED'
                        ? `Verification was unsuccessful: ${kycStatus.rejectionReason || 'Please contact our support team for assistance.'}`
                        : 'Complete your identity verification to unlock premium banking features and higher transaction limits.'}
                  </p>
                  {kycStatus?.status !== 'REJECTED' && (
                    <div>
                      <Link
                        to="/kyc/submit"
                        className="inline-flex items-center px-4 py-2 bg-gradient-to-r from-amber-600 to-orange-600 hover:from-amber-500 hover:to-orange-500 text-white rounded-lg transition-all duration-300 transform hover:scale-105 shadow-lg hover:shadow-amber-500/25"
                      >
                        {kycStatus?.status === 'PENDING' ? 'Check Status' : 'Begin Verification'}
                        <RocketOutlined className="ml-2" />
                      </Link>
                    </div>
                  )}
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Financial Summary Cards */}
        <div className="mb-8">
          <h2 className="text-2xl font-bold mb-6 flex items-center text-cyan-300 animate-pulse-slow">
            <DashboardOutlined className="mr-3 text-cyan-400" />
            Quantum Wealth Overview
          </h2>
          
          <Row gutter={[16, 16]}>
            <Col xs={24} sm={12} lg={8}>
              <div className="h-full bg-gradient-to-br from-slate-800/50 to-slate-900/70 rounded-2xl p-6 backdrop-blur-md border border-cyan-500/20 shadow-xl transform transition-all duration-500 hover:scale-[1.02] hover:border-cyan-500/40 hover:shadow-cyan-500/10 relative overflow-hidden group">
                <div className="absolute -top-5 -right-5 w-20 h-20 bg-cyan-500/10 rounded-full blur-xl group-hover:scale-125 transition-transform duration-700"></div>
                <div className="flex items-start mb-4">
                  <div className="flex-shrink-0 p-3 bg-cyan-900/30 rounded-xl mr-4">
                    <WalletOutlined className="text-2xl text-cyan-400" />
                  </div>
                  <div className="flex-1">
                    <div className="text-gray-400 text-sm">Liquid Assets</div>
                    <div className="text-2xl font-bold text-cyan-300">{formatCurrency(getTotalBalance())}</div>
                  </div>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-sm text-cyan-300/80 flex items-center">
                    <RiseOutlined className="mr-1" />
                    2.3% growth this month
                  </span>
                  <button onClick={handleViewAllAccounts} className="text-cyan-400 hover:text-cyan-300 text-sm flex items-center transition-all duration-300 transform hover:translate-x-1">
                    Explore Details <EyeOutlined className="ml-1" />
                  </button>
                </div>
              </div>
            </Col>

            <Col xs={24} sm={12} lg={8}>
              <div className="h-full bg-gradient-to-br from-slate-800/50 to-slate-900/70 rounded-2xl p-6 backdrop-blur-md border border-purple-500/20 shadow-xl transform transition-all duration-500 hover:scale-[1.02] hover:border-purple-500/40 hover:shadow-purple-500/10 relative overflow-hidden group">
                <div className="absolute -top-5 -right-5 w-20 h-20 bg-purple-500/10 rounded-full blur-xl group-hover:scale-125 transition-transform duration-700"></div>
                <div className="flex items-start mb-4">
                  <div className="flex-shrink-0 p-3 bg-purple-900/30 rounded-xl mr-4">
                    <CreditCardOutlined className="text-2xl text-purple-400" />
                  </div>
                  <div className="flex-1">
                    <div className="text-gray-400 text-sm">Credit Portfolio</div>
                    <div className="text-2xl font-bold text-purple-300">{formatCurrency(getTotalLoanBalance())}</div>
                  </div>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-sm text-purple-300/80">
                    {loans.filter(loan => loan.status === 'DISBURSED').length} Active Facilities
                  </span>
                  <button onClick={handleViewAllLoans} className="text-purple-400 hover:text-purple-300 text-sm flex items-center transition-all duration-300 transform hover:translate-x-1">
                    Manage Loans <EyeOutlined className="ml-1" />
                  </button>
                </div>
              </div>
            </Col>

            <Col xs={24} sm={12} lg={8}>
              <div className="h-full bg-gradient-to-br from-slate-800/50 to-slate-900/70 rounded-2xl p-6 backdrop-blur-md border border-rose-500/20 shadow-xl transform transition-all duration-500 hover:scale-[1.02] hover:border-rose-500/40 hover:shadow-rose-500/10 relative overflow-hidden group">
                <div className="absolute -top-5 -right-5 w-20 h-20 bg-rose-500/10 rounded-full blur-xl group-hover:scale-125 transition-transform duration-700"></div>
                <div className="flex items-start mb-4">
                  <div className="flex-shrink-0 p-3 bg-rose-900/30 rounded-xl mr-4">
                    <DollarOutlined className="text-2xl text-rose-400" />
                  </div>
                  <div className="flex-1">
                    <div className="text-gray-400 text-sm">Monthly Expenditure</div>
                    <div className="text-2xl font-bold text-rose-300">{formatCurrency(12500)}</div>
                  </div>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-sm text-rose-300/80 flex items-center">
                    <FallOutlined className="mr-1" />
                    5.2% less than last month
                  </span>
                  <button onClick={handleViewAllTransactions} className="text-rose-400 hover:text-rose-300 text-sm flex items-center transition-all duration-300 transform hover:translate-x-1">
                    View Analysis <BarChartOutlined className="ml-1" />
                  </button>
                </div>
              </div>
            </Col>
          </Row>
        </div>

        {/* Quick Actions */}
        <div className="mb-8">
          <h2 className="text-2xl font-bold mb-6 flex items-center text-purple-300">
            <RocketOutlined className="mr-3 text-purple-400" />
            Quantum Access Portal
          </h2>
          <Row gutter={[16, 16]}>
            <Col xs={12} sm={8} md={6}>
              <Link to="/transactions/transfer">
                <div className="group bg-gradient-to-br from-gray-800/50 to-gray-900/70 rounded-2xl p-5 text-center backdrop-blur-md border border-cyan-500/20 shadow-lg transform transition-all duration-500 hover:scale-105 hover:border-cyan-500/40 hover:shadow-cyan-500/10 h-full flex flex-col items-center justify-center relative overflow-hidden">
                  <div className="absolute -top-5 -right-5 w-16 h-16 bg-cyan-500/10 rounded-full blur-lg group-hover:scale-150 transition-transform duration-700"></div>
                  <div className="p-3 bg-cyan-900/30 rounded-full mb-3 group-hover:scale-110 transition-transform duration-300 relative z-10">
                    <SendOutlined className="text-2xl text-cyan-400" />
                  </div>
                  <h3 className="font-semibold text-cyan-300 mb-1 relative z-10">Fund Transfer</h3>
                  <p className="text-gray-400 text-sm relative z-10">Seamless money movement</p>
                </div>
              </Link>
            </Col>

            <Col xs={12} sm={8} md={6}>
              <Link to="/beneficiaries">
                <div className="group bg-gradient-to-br from-gray-800/50 to-gray-900/70 rounded-2xl p-5 text-center backdrop-blur-md border border-emerald-500/20 shadow-lg transform transition-all duration-500 hover:scale-105 hover:border-emerald-500/40 hover:shadow-emerald-500/10 h-full flex flex-col items-center justify-center relative overflow-hidden">
                  <div className="absolute -top-5 -right-5 w-16 h-16 bg-emerald-500/10 rounded-full blur-lg group-hover:scale-150 transition-transform duration-700"></div>
                  <div className="p-3 bg-emerald-900/30 rounded-full mb-3 group-hover:scale-110 transition-transform duration-300 relative z-10">
                    <TeamOutlined className="text-2xl text-emerald-400" />
                  </div>
                  <h3 className="font-semibold text-emerald-300 mb-1 relative z-10">Beneficiary Hub</h3>
                  <p className="text-gray-400 text-sm relative z-10">Manage recipients</p>
                </div>
              </Link>
            </Col>

            <Col xs={12} sm={8} md={6}>
              <Link to="/loans/apply">
                <div className="group bg-gradient-to-br from-gray-800/50 to-gray-900/70 rounded-2xl p-5 text-center backdrop-blur-md border border-violet-500/20 shadow-lg transform transition-all duration-500 hover:scale-105 hover:border-violet-500/40 hover:shadow-violet-500/10 h-full flex flex-col items-center justify-center relative overflow-hidden">
                  <div className="absolute -top-5 -right-5 w-16 h-16 bg-violet-500/10 rounded-full blur-lg group-hover:scale-150 transition-transform duration-700"></div>
                  <div className="p-3 bg-violet-900/30 rounded-full mb-3 group-hover:scale-110 transition-transform duration-300 relative z-10">
                    <CreditCardOutlined className="text-2xl text-violet-400" />
                  </div>
                  <h3 className="font-semibold text-violet-300 mb-1 relative z-10">Credit Solutions</h3>
                  <p className="text-gray-400 text-sm relative z-10">Personalized lending</p>
                </div>
              </Link>
            </Col>

            <Col xs={12} sm={8} md={6}>
              <Link to="/emi-calculator">
                <div className="group bg-gradient-to-br from-gray-800/50 to-gray-900/70 rounded-2xl p-5 text-center backdrop-blur-md border border-amber-500/20 shadow-lg transform transition-all duration-500 hover:scale-105 hover:border-amber-500/40 hover:shadow-amber-500/10 h-full flex flex-col items-center justify-center relative overflow-hidden">
                  <div className="absolute -top-5 -right-5 w-16 h-16 bg-amber-500/10 rounded-full blur-lg group-hover:scale-150 transition-transform duration-700"></div>
                  <div className="p-3 bg-amber-900/30 rounded-full mb-3 group-hover:scale-110 transition-transform duration-300 relative z-10">
                    <CalculatorOutlined className="text-2xl text-amber-400" />
                  </div>
                  <h3 className="font-semibold text-amber-300 mb-1 relative z-10">EMI Planner</h3>
                  <p className="text-gray-400 text-sm relative z-10">Smart calculations</p>
                </div>
              </Link>
            </Col>
          </Row>
        </div>

        {/* Accounts and Transactions Section */}
        <div className="mb-8">
          <Tabs
            activeKey={activeTab}
            onChange={setActiveTab}
            className="dashboard-tabs"
            items={[
              {
                key: 'overview',
                label: (
                  <span className="flex items-center">
                    <PieChartOutlined className="mr-2" />
                    Wealth Dashboard
                  </span>
                ),
                children: (
                  <Row gutter={[24, 24]}>
                    <Col xs={24} lg={16}>
                      <div className="bg-gradient-to-br from-slate-800/50 to-slate-900/70 rounded-2xl p-6 backdrop-blur-md border border-indigo-500/20 shadow-xl h-full">
                        <div className="flex justify-between items-center mb-6">
                          <h3 className="text-xl font-semibold text-indigo-300 flex items-center">
                            <HistoryOutlined className="mr-2" />
                            Recent Financial Activity
                          </h3>
                          <button onClick={handleViewAllTransactions} className="text-indigo-400 hover:text-indigo-300 text-sm flex items-center transition-all duration-300">
                            View All <ArrowRightOutlined className="ml-1" />
                          </button>
                        </div>
                        
                        <div className="space-y-4">
                          {recentTransactions.slice(0, 5).map((transaction) => (
                            <div 
                              key={transaction.id} 
                              className="p-4 bg-gray-800/30 rounded-xl border border-gray-700/30 hover:border-indigo-500/30 transition-all duration-300 transform hover:scale-[1.01] group"
                            >
                              <div className="flex items-center justify-between">
                                <div className="flex items-center">
                                  <div className={`p-2 rounded-lg mr-4 ${
                                    transaction.type === 'CREDIT' 
                                      ? 'bg-green-900/30 text-green-400' 
                                      : 'bg-red-900/30 text-red-400'
                                  }`}>
                                    {getTransactionIcon(transaction.type)}
                                  </div>
                                  <div>
                                    <div className="font-medium text-gray-200 group-hover:text-indigo-200 transition-colors duration-300">
                                      {transaction.description}
                                    </div>
                                    <div className="text-sm text-gray-400">
                                      {new Date(transaction.timestamp).toLocaleDateString('en-US', {
                                        year: 'numeric',
                                        month: 'short',
                                        day: 'numeric',
                                        hour: '2-digit',
                                        minute: '2-digit'
                                      })}
                                    </div>
                                  </div>
                                </div>
                                <div className={`text-lg font-semibold ${
                                  transaction.type === 'CREDIT' ? 'text-green-400' : 'text-red-400'
                                }`}>
                                  {transaction.type === 'CREDIT' ? '+' : '-'}{formatCurrency(transaction.amount)}
                                </div>
                              </div>
                            </div>
                          ))}
                        </div>
                      </div>
                    </Col>

                    <Col xs={24} lg={8}>
                      <div className="bg-gradient-to-br from-slate-800/50 to-slate-900/70 rounded-2xl p-6 backdrop-blur-md border border-cyan-500/20 shadow-xl h-full">
                        <h3 className="text-xl font-semibold text-cyan-300 mb-6 flex items-center">
                          <BankOutlined className="mr-2" />
                          Account Portfolio
                        </h3>
                        
                        <div className="space-y-5">
                          {accounts.map((account) => (
                            <div key={account.accountNumber} className="p-4 bg-gray-800/30 rounded-xl border border-gray-700/30 hover:border-cyan-500/30 transition-all duration-300 group">
                              <div className="flex justify-between items-start mb-3">
                                <div>
                                  <div className="font-medium text-gray-200 group-hover:text-cyan-200 transition-colors duration-300">
                                    {account.accountName}
                                  </div>
                                  <div className="text-sm text-gray-400">
                                    ●●●● {account.accountNumber.slice(-4)}
                                  </div>
                                </div>
                                <Tag color={account.status === 'ACTIVE' ? 'green' : 'red'} className="m-0">
                                  {account.status}
                                </Tag>
                              </div>
                              <div className="text-2xl font-bold text-cyan-300 mb-3">
                                {formatCurrency(account.balance)}
                              </div>
                              <div className="flex justify-between items-center">
                                <span className="text-sm text-cyan-300/80">Available Balance</span>
                                <Link 
                                  to={`/accounts/${account.accountNumber}`}
                                  className="text-cyan-400 hover:text-cyan-300 text-xs flex items-center transition-all duration-300 transform hover:translate-x-1"
                                >
                                  Details <EyeOutlined className="ml-1" />
                                </Link>
                              </div>
                            </div>
                          ))}
                          
                          <button onClick={handleOpenNewAccount}>
                            <div className="p-4 bg-gray-800/30 rounded-xl border border-dashed border-gray-600/50 hover:border-cyan-500/50 transition-all duration-300 group text-center cursor-pointer">
                              <PlusOutlined className="text-cyan-400 text-lg mb-2" />
                              <div className="text-cyan-400 group-hover:text-cyan-300 transition-colors duration-300">
                                Open New Account
                              </div>
                            </div>
                          </button>
                        </div>
                      </div>
                    </Col>
                  </Row>
                )
              },
              {
                key: 'loans',
                label: (
                  <span className="flex items-center">
                    <CreditCardOutlined className="mr-2" />
                    Credit Facilities
                  </span>
                ),
                children: (
                  <div className="bg-gradient-to-br from-slate-800/50 to-slate-900/70 rounded-2xl p-6 backdrop-blur-md border border-purple-500/20 shadow-xl">
                    <div className="flex justify-between items-center mb-6">
                      <h3 className="text-xl font-semibold text-purple-300 flex items-center">
                        <CreditCardOutlined className="mr-2" />
                        Active Credit Facilities
                      </h3>
                      <Link to="/loans/apply" className="text-purple-400 hover:text-purple-300 text-sm flex items-center transition-all duration-300">
                        Apply for New Loan <PlusOutlined className="ml-1" />
                      </Link>
                    </div>
                    
                    {loans.length > 0 ? (
                      <div className="space-y-5">
                        {loans.map((loan) => (
                          <div key={loan.id} className="p-5 bg-gray-800/30 rounded-xl border border-gray-700/30 hover:border-purple-500/30 transition-all duration-300 group">
                            <div className="flex justify-between items-start mb-4">
                              <div>
                                <div className="font-medium text-gray-200 group-hover:text-purple-200 transition-colors duration-300">
                                  {loan.productName}
                                </div>
                                <div className="text-sm text-gray-400">
                                  Disbursed on {new Date(loan.disbursementDate).toLocaleDateString()}
                                </div>
                              </div>
                              <Tag color={getLoanStatusColor(loan.status)} className="m-0">
                                {loan.status}
                              </Tag>
                            </div>
                            
                            <Row gutter={16} className="mb-4">
                              <Col xs={12} md={8}>
                                <div className="text-sm text-gray-400">Loan Amount</div>
                                <div className="text-lg font-semibold text-purple-300">
                                  {formatCurrency(loan.loanAmount)}
                                </div>
                              </Col>
                              <Col xs={12} md={8}>
                                <div className="text-sm text-gray-400">Outstanding</div>
                                <div className="text-lg font-semibold text-amber-300">
                                  {formatCurrency(loan.outstandingBalance)}
                                </div>
                              </Col>
                              <Col xs={24} md={8}>
                                <div className="text-sm text-gray-400">Progress</div>
                                <Progress
                                  percent={Math.round((1 - (loan.outstandingBalance / loan.loanAmount)) * 100)}
                                  size="small"
                                  strokeColor={{
                                    '0%': '#8b5cf6',
                                    '100%': '#a855f7',
                                  }}
                                  className="mt-1"
                                />
                              </Col>
                            </Row>
                            
                            <div className="flex justify-between items-center">
                              <span className="text-sm text-purple-300/80">
                                Next EMI Due: {new Date(Date.now() + 15 * 24 * 60 * 60 * 1000).toLocaleDateString()}
                              </span>
                              <Link 
                                to={`/loans/${loan.id}`}
                                className="text-purple-400 hover:text-purple-300 text-sm flex items-center transition-all duration-300 transform hover:translate-x-1"
                              >
                                View Details <EyeOutlined className="ml-1" />
                              </Link>
                            </div>
                          </div>
                        ))}
                      </div>
                    ) : (
                      <div className="text-center py-10">
                        <CreditCardOutlined className="text-4xl text-purple-400 mb-4 opacity-50" />
                        <h4 className="text-lg text-gray-400 mb-2">No Active Loans</h4>
                        <p className="text-gray-500 mb-4">You don't have any active credit facilities at the moment.</p>
                        <Link 
                          to="/loans/apply"
                          className="inline-flex items-center px-4 py-2 bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-500 hover:to-indigo-500 text-white rounded-lg transition-all duration-300 transform hover:scale-105"
                        >
                          Apply for a Loan
                          <RocketOutlined className="ml-2" />
                        </Link>
                      </div>
                    )}
                  </div>
                )
              }
            ]}
          />
        </div>

        {/* Financial Health & Rewards */}
        <div className="mb-8">
          <Row gutter={[24, 24]}>
            <Col xs={24} lg={12}>
              <div className="bg-gradient-to-br from-slate-800/50 to-slate-900/70 rounded-2xl p-6 backdrop-blur-md border border-green-500/20 shadow-xl h-full">
                <h3 className="text-xl font-semibold text-green-300 mb-6 flex items-center">
                  <SafetyCertificateOutlined className="mr-2" />
                  Financial Wellness Score
                </h3>
                
                <div className="text-center mb-6">
                  <div className="relative inline-block">
                    <Progress
                      type="circle"
                      percent={78}
                      size={120}
                      strokeColor={{
                        '0%': '#10b981',
                        '100%': '#34d399',
                      }}
                      format={percent => (
                        <div className="text-center">
                          <div className="text-2xl font-bold text-green-300">{percent}</div>
                          <div className="text-xs text-green-400">Excellent</div>
                        </div>
                      )}
                    />
                  </div>
                </div>
                
                <div className="space-y-3">
                  <div className="flex justify-between items-center">
                    <span className="text-gray-400">Savings Ratio</span>
                    <div className="flex items-center">
                      <Progress
                        percent={65}
                        size="small"
                        showInfo={false}
                        strokeColor="#10b981"
                        className="w-20 mr-2"
                      />
                      <span className="text-green-400">65%</span>
                    </div>
                  </div>
                  <div className="flex justify-between items-center">
                    <span className="text-gray-400">Debt-to-Income</span>
                    <div className="flex items-center">
                      <Progress
                        percent={22}
                        size="small"
                        showInfo={false}
                        strokeColor="#10b981"
                        className="w-20 mr-2"
                      />
                      <span className="text-green-400">22%</span>
                    </div>
                  </div>
                  <div className="flex justify-between items-center">
                    <span className="text-gray-400">Credit Utilization</span>
                    <div className="flex items-center">
                      <Progress
                        percent={35}
                        size="small"
                        showInfo={false}
                        strokeColor="#f59e0b"
                        className="w-20 mr-2"
                      />
                      <span className="text-amber-400">35%</span>
                    </div>
                  </div>
                </div>
                
                <div className="mt-6 pt-4 border-t border-gray-700/30">
                  <Link to="/financial-health" className="text-green-400 hover:text-green-300 text-sm flex items-center justify-center transition-all duration-300">
                    View Detailed Analysis <BarChartOutlined className="ml-2" />
                  </Link>
                </div>
              </div>
            </Col>

            <Col xs={24} lg={12}>
              <div className="bg-gradient-to-br from-slate-800/50 to-slate-900/70 rounded-2xl p-6 backdrop-blur-md border border-amber-500/20 shadow-xl h-full">
                <h3 className="text-xl font-semibold text-amber-300 mb-6 flex items-center">
                  <GiftOutlined className="mr-2" />
                  Quantum Rewards
                </h3>
                
                <div className="text-center mb-6">
                  <div className="relative inline-block">
                    <div className="relative">
                      <CrownOutlined className="text-5xl text-amber-400 mb-3" />
                      <div className="absolute -top-2 -right-3">
                        <div className="bg-amber-600 text-amber-100 text-xs font-bold px-2 py-1 rounded-full">
                          7
                        </div>
                      </div>
                    </div>
                    <div className="text-2xl font-bold text-amber-300">4,250</div>
                    <div className="text-sm text-amber-400">Reward Points</div>
                  </div>
                </div>
                
                <div className="space-y-4 mb-6">
                  <div className="flex items-center p-3 bg-amber-900/20 rounded-lg">
                    <div className="p-2 bg-amber-800/30 rounded-md mr-3">
                      <StarOutlined className="text-amber-400" />
                    </div>
                    <div className="flex-1">
                      <div className="font-medium text-amber-200">5x Points on Dining</div>
                      <div className="text-xs text-amber-400/80">Earn extra points at restaurants</div>
                    </div>
                  </div>
                  
                  <div className="flex items-center p-3 bg-amber-900/20 rounded-lg">
                    <div className="p-2 bg-amber-800/30 rounded-md mr-3">
                      <GlobalOutlined className="text-amber-400" />
                    </div>
                    <div className="flex-1">
                      <div className="font-medium text-amber-200">Travel Benefits</div>
                      <div className="text-xs text-amber-400/80">Complimentary airport lounge access</div>
                    </div>
                  </div>
                </div>
                
                <div className="mt-6 pt-4 border-t border-gray-700/30 text-center">
                  <Link to="/rewards" className="inline-flex items-center px-4 py-2 bg-gradient-to-r from-amber-600 to-orange-600 hover:from-amber-500 hover:to-orange-500 text-white rounded-lg transition-all duration-300 transform hover:scale-105">
                    Explore Rewards
                    <GiftOutlined className="ml-2" />
                  </Link>
                </div>
              </div>
            </Col>
          </Row>
        </div>

        {/* Advanced Banking Features */}
        <div className="mb-8">
          <h2 className="text-2xl font-bold mb-6 flex items-center text-indigo-300">
            <RadarChartOutlined className="mr-3 text-indigo-400" />
            Advanced Financial Tools
          </h2>
          
          <Row gutter={[16, 16]}>
            <Col xs={24} sm={12} md={8}>
              <div className="group bg-gradient-to-br from-gray-800/50 to-gray-900/70 rounded-2xl p-6 backdrop-blur-md border border-indigo-500/20 shadow-lg transform transition-all duration-500 hover:scale-[1.02] hover:border-indigo-500/40 hover:shadow-indigo-500/10 h-full relative overflow-hidden">
                <div className="absolute -top-5 -right-5 w-20 h-20 bg-indigo-500/10 rounded-full blur-xl group-hover:scale-125 transition-transform duration-700"></div>
                <div className="flex items-start mb-4">
                  <div className="flex-shrink-0 p-3 bg-indigo-900/30 rounded-xl mr-4">
                    <PartitionOutlined className="text-2xl text-indigo-400" />
                  </div>
                  <div className="flex-1">
                    <h3 className="font-semibold text-indigo-300 mb-2">Portfolio Analytics</h3>
                    <p className="text-gray-400 text-sm">
                      Advanced analytics for your investment portfolio with AI-powered insights
                    </p>
                  </div>
                </div>
                <div className="mt-4">
                  <Link to="/portfolio-analytics" className="text-indigo-400 hover:text-indigo-300 text-sm flex items-center transition-all duration-300">
                    Explore Analytics <ArrowRightOutlined className="ml-1" />
                  </Link>
                </div>
              </div>
            </Col>

            <Col xs={24} sm={12} md={8}>
              <div className="group bg-gradient-to-br from-gray-800/50 to-gray-900/70 rounded-2xl p-6 backdrop-blur-md border border-cyan-500/20 shadow-lg transform transition-all duration-500 hover:scale-[1.02] hover:border-cyan-500/40 hover:shadow-cyan-500/10 h-full relative overflow-hidden">
                <div className="absolute -top-5 -right-5 w-20 h-20 bg-cyan-500/10 rounded-full blur-xl group-hover:scale-125 transition-transform duration-700"></div>
                <div className="flex items-start mb-4">
                  <div className="flex-shrink-0 p-3 bg-cyan-900/30 rounded-xl mr-4">
                    <NodeIndexOutlined className="text-2xl text-cyan-400" />
                  </div>
                  <div className="flex-1">
                    <h3 className="font-semibold text-cyan-300 mb-2">AI Financial Advisor</h3>
                    <p className="text-gray-400 text-sm">
                      Get personalized financial advice powered by quantum computing algorithms
                    </p>
                  </div>
                </div>
                <div className="mt-4">
                  <Link to="/ai-advisor" className="text-cyan-400 hover:text-cyan-300 text-sm flex items-center transition-all duration-300">
                    Consult Advisor <ArrowRightOutlined className="ml-1" />
                  </Link>
                </div>
              </div>
            </Col>

            <Col xs={24} sm={12} md={8}>
              <div className="group bg-gradient-to-br from-gray-800/50 to-gray-900/70 rounded-2xl p-6 backdrop-blur-md border border-purple-500/20 shadow-lg transform transition-all duration-500 hover:scale-[1.02] hover:border-purple-500/40 hover:shadow-purple-500/10 h-full relative overflow-hidden">
                <div className="absolute -top-5 -right-5 w-20 h-20 bg-purple-500/10 rounded-full blur-xl group-hover:scale-125 transition-transform duration-700"></div>
                <div className="flex items-start mb-4">
                  <div className="flex-shrink-0 p-3 bg-purple-900/30 rounded-xl mr-4">
                    <GatewayOutlined className="text-2xl text-purple-400" />
                  </div>
                  <div className="flex-1">
                    <h3 className="font-semibold text-purple-300 mb-2">Blockchain Security</h3>
                    <p className="text-gray-400 text-sm">
                      Enhanced security with blockchain technology for all your transactions
                    </p>
                  </div>
                </div>
                <div className="mt-4">
                  <Link to="/security" className="text-purple-400 hover:text-purple-300 text-sm flex items-center transition-all duration-300">
                    Security Center <ArrowRightOutlined className="ml-1" />
                  </Link>
                </div>
              </div>
            </Col>
          </Row>
        </div>

        {/* Footer */}
        <div className="text-center text-gray-500 text-sm py-8 border-t border-gray-800/50 mt-8">
          <div className="flex flex-wrap justify-center items-center gap-6 mb-4">
            <span className="flex items-center">
              <SecurityScanOutlined className="mr-2 text-green-500" />
              <span>256-bit SSL Encryption</span>
            </span>
            <span className="flex items-center">
              <GlobalOutlined className="mr-2 text-blue-500" />
              <span>Global Banking Standards</span>
            </span>
            <span className="flex items-center">
              <DeploymentUnitOutlined className="mr-2 text-purple-500" />
              <span>Quantum Secure Technology</span>
            </span>
          </div>
          <p>© 2023 Quantum Banking. Next Generation Financial Services.</p>
          <p className="mt-1">Your financial security is our top priority.</p>
        </div>
      </div>

      {/* Global Styles */}
      <style jsx="true">{`
        @keyframes textShimmer {
          0% { background-position: -100% 0; }
          100% { background-position: 200% 0; }
        }
        
        @keyframes float {
          0%, 100% { transform: translateY(0) rotate(0deg); }
          50% { transform: translateY(-20px) rotate(5deg); }
        }
        
        @keyframes floatReverse {
          0%, 100% { transform: translateY(0) rotate(0deg); }
          50% { transform: translateY(20px) rotate(-5deg); }
        }
        
        @keyframes floatSlow {
          0%, 100% { transform: translateY(0) rotate(0deg); }
          50% { transform: translateY(-10px) rotate(3deg); }
        }
        
        @keyframes pulseSlow {
          0%, 100% { opacity: 1; }
          50% { opacity: 0.7; }
        }
        
        @keyframes pulseMedium {
          0%, 100% { opacity: 1; }
          50% { opacity: 0.8; }
        }
        
        @keyframes pulseFast {
          0%, 100% { opacity: 1; }
          50% { opacity: 0.9; }
        }
        
        @keyframes pulseSlower {
          0%, 100% { opacity: 1; }
          50% { opacity: 0.6; }
        }
        
        @keyframes borderPulse {
          0%, 100% { border-color: rgba(6, 182, 212, 0.5); box-shadow: 0 0 0 rgba(6, 182, 212, 0.2); }
          50% { border-color: rgba(6, 182, 212, 0.8); box-shadow: 0 0 20px rgba(6, 182, 212, 0.4); }
        }
        
        @keyframes fadeInUp {
          from { opacity: 0; transform: translateY(30px); }
          to { opacity: 1; transform: translateY(0); }
        }
        
        @keyframes spinSlow {
          from { transform: rotate(0deg); }
          to { transform: rotate(360deg); }
        }
        
        .animate-text-shimmer {
          background: linear-gradient(
            to right,
            #22d3ee 0%,
            #a5b4fc 20%,
            #22d3ee 40%,
            #22d3ee 100%
          );
          background-size: 200% auto;
          background-clip: text;
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          animation: textShimmer 3s linear infinite;
        }
        
        .animate-float {
          animation: float 8s ease-in-out infinite;
        }
        
        .animate-float-reverse {
          animation: floatReverse 10s ease-in-out infinite;
        }
        
        .animate-float-slow {
          animation: floatSlow 12s ease-in-out infinite;
        }
        
        .animate-pulse-slow {
          animation: pulseSlow 4s ease-in-out infinite;
        }
        
        .animate-pulse-medium {
          animation: pulseMedium 3s ease-in-out infinite;
        }
        
        .animate-pulse-fast {
          animation: pulseFast 2s ease-in-out infinite;
        }
        
        .animate-pulse-slower {
          animation: pulseSlower 5s ease-in-out infinite;
        }
        
        .animate-border-pulse {
          animation: borderPulse 3s ease-in-out infinite;
        }
        
        .animate-fade-in-up {
          animation: fadeInUp 0.8s ease-out;
        }
        
        .animate-spin-slow {
          animation: spinSlow 8s linear infinite;
        }
        
        .dashboard-tabs .ant-tabs-nav::before {
          border-bottom: 1px solid rgba(255, 255, 255, 0.1) !important;
        }
        
        .dashboard-tabs .ant-tabs-tab {
          color: rgba(255, 255, 255, 0.6) !important;
          padding: 12px 16px !important;
          font-weight: 500;
          border-radius: 8px 8px 0 0 !important;
          margin-right: 4px !important;
        }
        
        .dashboard-tabs .ant-tabs-tab:hover {
          color: rgba(255, 255, 255, 0.9) !important;
          background: rgba(255, 255, 255, 0.05) !important;
        }
        
        .dashboard-tabs .ant-tabs-tab.ant-tabs-tab-active {
          color: rgba(255, 255, 255, 0.9) !important;
          background: rgba(255, 255, 255, 0.1) !important;
        }
        
        .dashboard-tabs .ant-tabs-ink-bar {
          background: linear-gradient(90deg, #22d3ee, #a5b4fc) !important;
          height: 3px !important;
        }
        
        .dashboard-title {
          font-size: 2.5rem;
          font-weight: 800;
          letter-spacing: -0.025em;
        }
        
        @media (max-width: 768px) {
          .dashboard-title {
            font-size: 2rem;
          }
        }
      `}</style>
    </div>
  );
};

export default CustomerDashboard;