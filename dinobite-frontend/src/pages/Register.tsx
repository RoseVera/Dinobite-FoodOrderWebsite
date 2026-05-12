import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/Login.css';
import { useUserStore } from "@/store/UserStore";
import axios, { AxiosError } from 'axios';

const Register = () => {
    const [step, setStep] = useState<'CUSTOMER' | 'RESTAURANT' | 'COURIER'>('CUSTOMER');
    const [showPassword, setShowPassword] = useState(false);
    const [agreementChecked, setAgreementChecked] = useState(false);
    const [loading, setLoading] = useState(false);
    const [formData, setFormData] = useState({
        mail: '', password: '', name: '', city: '', address: '', phone: '', birthDate: '',
        businessOwner: '', ownerMail: '', hours: '', cuisine: '', deliveryRange: '', logo: ''
    });
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const setUser = useUserStore((state) => state.setUser);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const registerUser = async (type: string) => {
        try {
            const response = await axios.post('http://localhost:9090/api/auth/register', {
                mail: formData.mail,
                password: formData.password,
                name: formData.name,
                type,
                city: formData.city
            }, {
                withCredentials: true, // fetch’te credentials: 'include' vardı, axios'ta bu
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            return response.data;

        } catch (error) {
            const axiosError = error as AxiosError<any>;
            console.error("Register error:", axiosError);

            if (axiosError.response?.data?.message) {
                throw new Error(axiosError.response.data.message);
            } else {
                throw new Error('User registration failed.');
            }
        }
    };

    const handleRegister = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        if (!agreementChecked) {
            setError("You must accept the terms and conditions to register.");
            setLoading(false);
            return;
        }

        try {
            const user = await registerUser(step);

            let additionalData: any = {};
            let endpoint = '';
            let redirectPath = '/';

            if (step === 'CUSTOMER') {
                endpoint = 'customers';
                additionalData = { userId: user.userId, address: formData.address, phone: formData.phone, birthDate: formData.birthDate };
            } else if (step === 'RESTAURANT') {
                endpoint = 'restaurants';
                additionalData = {
                    userId: user.userId, businessOwner: formData.businessOwner, ownerMail: formData.ownerMail,
                    phone: formData.phone, hours: formData.hours, cuisine: formData.cuisine,
                    deliveryRange: Number(formData.deliveryRange), address: formData.address, logo: formData.logo
                };
                redirectPath = '/restaurant-dashboard';
            } else if (step === 'COURIER') {
                endpoint = 'couriers';
                additionalData = {
                    userId: user.userId, availability: false, photo: formData.logo,
                    birthDate: formData.birthDate, status: 'INACTIVE'
                };
                redirectPath = `/courier-profile`;
            }

            const res = await fetch(`http://localhost:9090/api/v1/${endpoint}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(additionalData)
            });

            const result = await res.json();

            if (!res.ok) {
                 if (user && user.userId) {
                    await fetch(`http://localhost:9090/api/v1/users/${user.userId}`, {
                        method: 'DELETE'
                    });
                    console.log("silindddi")
                }
                throw new Error(result.message || `${step} creation failed.`);
               
            }

            setUser({
                id: user.userId, name: formData.name, userType: step, mail: formData.mail,
                [`${step.toLowerCase()}Id`]: result.id
            });

            alert(`${step} registered successfully!`);
            navigate(step === 'COURIER' ? `${redirectPath}/${result.id}` : redirectPath);
        } catch (err: any) {
            setError(err.message || 'Something went wrong.');
        } finally {
            setLoading(false);
        }
    };

    const renderInputs = () => {
        const common = (
            <>
                <input type="email" name="mail" placeholder="Email" value={formData.mail} onChange={handleChange} required />
                <div className="password-wrapper">
                    <input type={showPassword ? "text" : "password"} name="password" placeholder="Password"
                        value={formData.password} onChange={handleChange} required />
                    <span onClick={() => setShowPassword(!showPassword)} className="toggle-password">
                        {showPassword ? "🙈" : "👁️"}
                    </span>
                </div>
                <input type="text" name="name" placeholder={step === 'RESTAURANT' ? "Restaurant Name" : "Name"} value={formData.name} onChange={handleChange} required />
                <input type="text" name="city" placeholder="City" value={formData.city} onChange={handleChange} required />
            </>
        );

        const customerFields = (
            <>
                <input type="text" name="address" placeholder="Address" value={formData.address} onChange={handleChange} />
                <input type="text" name="phone" placeholder="Phone" value={formData.phone} onChange={handleChange} />
                <input type="date" name="birthDate" value={formData.birthDate} onChange={handleChange} required />
            </>
        );

        const restaurantFields = (
            <>
                <input type="text" name="businessOwner" placeholder="Business Owner" value={formData.businessOwner} onChange={handleChange} />
                <input type="email" name="ownerMail" placeholder="Owner Email" value={formData.ownerMail} onChange={handleChange} />
                <input type="text" name="phone" placeholder="Phone" value={formData.phone} onChange={handleChange} />
                <input type="text" name="hours" placeholder="Opening Hours" value={formData.hours} onChange={handleChange} />
                <input type="text" name="cuisine" placeholder="Cuisine" value={formData.cuisine} onChange={handleChange} />
                <input type="number" name="deliveryRange" placeholder="Delivery Range (km)" value={formData.deliveryRange} onChange={handleChange} />
                <input type="text" name="address" placeholder="Address" value={formData.address} onChange={handleChange} />
                <input type="text" name="logo" placeholder="Logo URL" value={formData.logo} onChange={handleChange} />
            </>
        );

        const courierFields = (
            <>
                <input type="date" name="birthDate" value={formData.birthDate} onChange={handleChange} required />
                <input type="text" name="logo" placeholder="Photo URL" value={formData.logo} onChange={handleChange} />
            </>
        );

        return (
            <>
                {common}
                {step === 'CUSTOMER' && customerFields}
                {step === 'RESTAURANT' && restaurantFields}
                {step === 'COURIER' && courierFields}
            </>
        );
    };

    return (
        <div className="register-container">
            <div className="register-wrapper">
                <div className="register-alt-buttons">
                    {['CUSTOMER', 'RESTAURANT', 'COURIER'].filter(role => role !== step).map(role => (
                        <button key={role} type="button" onClick={() => setStep(role as any)}>
                            Sign up as {role.charAt(0) + role.slice(1).toLowerCase()}
                        </button>
                    ))}
                </div>
                <div className="register-form">
                    <h2 style={{ textAlign: 'center', fontSize: '2rem', color: '#ff6600', textShadow: '1px 1px 2px black', marginBottom: '1rem' }}>
                        Register as {step.charAt(0) + step.slice(1).toLowerCase()}
                    </h2>
                    <form onSubmit={handleRegister}>
                        {renderInputs()}
                        {error && <p className="error-text">{error}</p>}
                        <div style={{ display: 'flex', alignItems: 'center', marginTop: '1rem' }}>
                            <input type="checkbox" checked={agreementChecked} onChange={() => setAgreementChecked(!agreementChecked)} style={{ marginRight: '-80px' }} />
                            <label style={{ fontSize: '0.9rem' }}>
                                I have read and accept the Membership and Data Protection Agreement (KVKK).
                            </label>
                        </div>
                        {loading ? (
                            <p style={{ textAlign: 'center', fontWeight: 'bold', marginTop: '1rem' }}>🌳 Please wait, registering...🌳</p>
                        ) : (
                            <button type="submit">Register</button>
                        )}
                    </form>
                </div>
            </div>
        </div>
    );
};

export default Register;
