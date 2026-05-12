import React, { useState, useEffect, useRef } from 'react';
import { useUserStore } from '@/store/UserStore';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

// Prizes and their corresponding values/colors
const prizes = [
    { text: '10% Discount', value: 10, color: '#4CAF50' }, // Green
    { text: '5% Discount',  value: 5,  color: '#FFEB3B' }, // Yellow
    { text: '15% Discount', value: 15, color: '#9C27B0' }, // Purple
    { text: '10% Discount', value: 10, color: '#2196F3' }, // Blue
    { text: '20% Discount', value: 20, color: '#F44336' }, // Red
    { text: '15% Discount', value: 15, color: '#FF9800' }, // Orange
];

// Mapping used for creating the request DTO
const prizeMappings: { [key: string]: { type: 'PERCENTAGE'; value: number } } = {
    '5% Discount':  { type: 'PERCENTAGE', value: 5 },
    '10% Discount': { type: 'PERCENTAGE', value: 10 },
    '15% Discount': { type: 'PERCENTAGE', value: 15 },
    '20% Discount': { type: 'PERCENTAGE', value: 20 },
};

// Type for storing winning details (needed for displaying image and message)
type PrizeWonInfo = {
    code: string;
    discountPercent: number;
};

// --- CSS Spin Wheel Component (Using Existing Backend Endpoint) ---
const SpinWheelCss: React.FC = () => {
    const [rotation, setRotation] = useState<number>(0);
    const [isSpinning, setIsSpinning] = useState<boolean>(false);
    const [resultMessage, setResultMessage] = useState<string | null>(null);
    const [errorMessage, setErrorMessage] = useState<string | null>(null);
    const user = useUserStore((state) => state.user);
    const navigate = useNavigate();
    const [customerId, setCustomerId] = useState<number | null>(null);
    const [fetchingCustomerId, setFetchingCustomerId] = useState<boolean>(false);
    const wheelRef = useRef<HTMLDivElement>(null);
    const [winningPrizeIndex, setWinningPrizeIndex] = useState<number | null>(null);
    const [prizeWonDetails, setPrizeWonDetails] = useState<PrizeWonInfo | null>(null);

    // Fetch Customer ID Effect (No changes needed here)
    useEffect(() => {
        const fetchCustomerId = async () => {
            if (user?.id && !customerId && !fetchingCustomerId) {
                setFetchingCustomerId(true);
                setErrorMessage(null);
                setPrizeWonDetails(null);
                try {
                    console.log(`Fetching customer ID for userId: ${user.id}`);
                    const response = await axios.get(
                        `http://localhost:9090/api/v1/customers/users/${user.id}`,
                        { withCredentials: true }
                    );
                    if (response.data && response.data.id) {
                        setCustomerId(response.data.id);
                        console.log(`Customer ID found: ${response.data.id}`);
                    } else { setErrorMessage('Could not find your customer profile.'); }
                } catch (error) { setErrorMessage('Error fetching profile.');
                } finally { setFetchingCustomerId(false); }
            } else if (!user) {
                setCustomerId(null); setPrizeWonDetails(null);
            }
        };
        fetchCustomerId();
    }, [user, customerId, fetchingCustomerId]);

    // Spin Button Handler (No changes needed here)
    const handleSpinClick = () => {
        if (!user) { setErrorMessage('Please log in.'); navigate('/login2'); return; }
        if (fetchingCustomerId) { setErrorMessage('Loading profile...'); return; }
        if (!customerId) { setErrorMessage('Could not load profile.'); return; }
        if (isSpinning) return;

        setIsSpinning(true);
        setResultMessage(null);
        setErrorMessage(null);
        setPrizeWonDetails(null);
        setWinningPrizeIndex(null);

        const randomIndex = Math.floor(Math.random() * prizes.length);
        setWinningPrizeIndex(randomIndex);
        console.log('Visually spinning to prize:', prizes[randomIndex].text);

        const segmentAngle = 360 / prizes.length;
        const targetAngle = -(randomIndex * segmentAngle + segmentAngle / 2);
        const extraRotations = 360 * 5;
        const randomOffset = (Math.random() - 0.5) * (segmentAngle * 0.8);
        const finalRotation = rotation + extraRotations + targetAngle + randomOffset;

        setRotation(finalRotation);
    };

    // --- API Call Handler (Sends DTO to existing endpoint) ---
    const handleApiCall = async (prizeIndex: number) => {
        if (prizeIndex === null || !customerId) {
            setErrorMessage("Could not process spin result due to profile error.");
            setIsSpinning(false);
            return;
        }

        const winningPrize = prizes[prizeIndex];
        const prizeDetails = prizeMappings[winningPrize.text]; // Get details from mapping

        // Check if it's a valid prize type we can create a coupon for
        if (!prizeDetails) {
            console.warn(`Prize type "${winningPrize.text}" not configured for coupon creation.`);
            // Maybe show a "Try Again" or different message if needed based on design
            setResultMessage(`You landed on: ${winningPrize.text} (No coupon awarded for this prize)`);
            setIsSpinning(false); // Ensure spinning stops
            return;
        }

        // --- Frontend Creates Coupon Details ---
        const couponCode = `SPIN-${Date.now().toString(36).toUpperCase()}-${customerId}`;
        const expirationDate = new Date();
        expirationDate.setDate(expirationDate.getDate() + 7); // 7 days validity

        // Prepare the DTO backend expects for the *existing* endpoint
        const couponRequest = {
            code: couponCode,
            discountPercent: prizeDetails.value,
            expirationDate: expirationDate.toISOString(), // Send as ISO string
            customerId: customerId
            // customerId is in the URL path, not in the body for this endpoint
        };
        // --- ---

        setResultMessage(`Processing your ${winningPrize.text} prize...`);
        setErrorMessage(null);

        try {
            console.log('Attempting to create coupon via existing endpoint:', couponRequest, 'for customerId:', customerId);

            // --- Call the EXISTING Backend Endpoint ---
            const response = await axios.post(
                `http://localhost:9090/api/v1/customers/${customerId}/coupons`, // The original endpoint
                couponRequest, // Send the DTO created by frontend
                { withCredentials: true }
            );
            // --- ---

            if (response.status === 201 && response.data) {
                console.log('Coupon created successfully:', response.data);
                setResultMessage(null); // Clear processing message
                // Store details to show the winning image/message
                setPrizeWonDetails({
                    code: couponRequest.code, // Use the code we generated
                    discountPercent: couponRequest.discountPercent // Use the percent we generated
                });
            } else {
                throw new Error(`Unexpected API response status: ${response.status}`);
            }
        } catch (error: any) {
            console.error('Failed to create coupon via API:', error);
            let specificErrorMessage = "An error occurred while claiming your prize.";
            if (error.response) {
                // Show backend error (e.g., the "already spun this week" error)
                specificErrorMessage = error.response.data?.message || `Request failed (Status: ${error.response.status})`;
            } else if (error.request) { specificErrorMessage = 'Could not connect to the server.'; }
            else { specificErrorMessage = error.message || specificErrorMessage; }
            setErrorMessage(specificErrorMessage);
            setResultMessage(null);
            setPrizeWonDetails(null); // Clear win state on error
        } finally {
             // isSpinning is reset via transitionend
        }
    };

    // Transition End Handler (No changes needed here)
     useEffect(() => {
        const wheelElement = wheelRef.current;
        let transitionTimeoutId: NodeJS.Timeout | null = null;
        const handleTransitionEnd = () => {
            if (transitionTimeoutId) clearTimeout(transitionTimeoutId);
            if (isSpinning && winningPrizeIndex !== null) {
                 setIsSpinning(false);
                 handleApiCall(winningPrizeIndex);
            } else { setIsSpinning(false); }
        };
        if (isSpinning && wheelElement) {
             const duration = 4100;
             transitionTimeoutId = setTimeout(() => { if (isSpinning) { console.warn('Transition timeout.'); handleTransitionEnd(); }}, duration);
         }
        if (wheelElement) wheelElement.addEventListener('transitionend', handleTransitionEnd);
        return () => {
            if (wheelElement) wheelElement.removeEventListener('transitionend', handleTransitionEnd);
            if (transitionTimeoutId) clearTimeout(transitionTimeoutId);
        };
    }, [winningPrizeIndex, customerId, isSpinning]);

    // Conic Gradient (No changes needed here)
    const conicGradient = `conic-gradient(${prizes.map((p, i) => `${p.color} ${i * (100 / prizes.length)}%, ${p.color} ${(i + 1) * (100 / prizes.length)}%`).join(', ')})`;

    // Get Image Path (No changes needed here)
    const getPrizeImagePath = (percentage: number | undefined): string => {
        if (percentage === undefined) return '/images/generic_prize.png';
        switch (percentage) {
            case 5:  return '/images/discount5.png';
            case 10: return '/images/discount10.png';
            case 15: return '/images/discount15.png';
            case 20: return '/images/discount20.png';
            default: return '/images/generic_prize.png';
        }
    };

    // Render (No major changes needed here, uses prizeWonDetails state)
    return (
        <div className="flex flex-col items-center justify-center p-6 sm:p-10 space-y-8 min-h-[80vh] bg-gray-100 overflow-hidden">
             <h1 className="text-3xl sm:text-4xl font-bold text-orange-600 mb-6 text-center">Spin the Wheel for Discounts!</h1>

            {/* Message Area */}
            <div className="h-16 flex items-center justify-center w-full max-w-md">
                 {errorMessage && <p className="text-red-600 text-base sm:text-lg font-semibold p-3 bg-red-100 rounded shadow text-center">{errorMessage}</p>}
                 {resultMessage && !prizeWonDetails && !errorMessage && <p className="text-blue-700 bg-blue-100 p-3 rounded shadow font-semibold text-lg sm:text-xl text-center">{resultMessage}</p>}
            </div>

             {/* Prize Won Display Area */}
             {prizeWonDetails && !errorMessage && (
                 <div className="text-center p-4 bg-white rounded-lg shadow-lg border border-green-300 w-full max-w-md mb-6 animate-jump-in">
                     <img
                         src={getPrizeImagePath(prizeWonDetails.discountPercent)}
                         alt={`You won ${prizeWonDetails.discountPercent}% discount!`}
                         className="w-24 h-24 mx-auto mb-3 object-contain"
                         onError={(e) => (e.currentTarget.src = '/images/generic_prize.png')}
                     />
                     <p className="text-xl font-bold text-green-700">Congratulations!</p>
                     <p className="text-lg text-gray-800">
                         You won {prizeWonDetails.discountPercent}% Discount!
                     </p>
                     <p className="text-sm text-gray-600 mt-1">
                         Coupon Code: <strong className="text-indigo-700">{prizeWonDetails.code}</strong>
                     </p>
                     <p className="text-xs text-gray-500 mt-2">(This coupon has been added to your account)</p>
                 </div>
             )}


            {/* Wheel Container */}
            <div className={`relative w-[300px] h-[300px] sm:w-[350px] sm:h-[350px] md:w-[400px] md:h-[400px] my-4 transition-opacity duration-500 ${prizeWonDetails ? 'opacity-50 pointer-events-none' : 'opacity-100'}`}>
                {/* Pointer */}
                <div className="absolute top-[-15px] left-1/2 transform -translate-x-1/2 z-10 animate-bounce" style={{width: 0, height: 0, borderLeft: '18px solid transparent', borderRight: '18px solid transparent', borderTop: '30px solid #333', filter: 'drop-shadow(0 2px 2px rgba(0,0,0,0.3))'}}></div>
                {/* The Wheel */}
                <div ref={wheelRef} className="w-full h-full rounded-full border-8 border-gray-500 shadow-2xl overflow-hidden" style={{background: conicGradient, transform: `rotate(${rotation}deg)`, transition: isSpinning ? 'transform 4s cubic-bezier(0.25, 1, 0.5, 1)' : 'none'}}></div>
            </div>

            {/* Spin Button */}
            <button
                onClick={handleSpinClick}
                disabled={isSpinning || !user || fetchingCustomerId || !customerId || !!prizeWonDetails}
                className={`px-10 py-5 text-xl sm:text-2xl text-white font-bold rounded-full shadow-lg transition duration-300 ease-in-out transform hover:scale-105 active:scale-95 ${
                isSpinning || !user || fetchingCustomerId || !customerId || prizeWonDetails
                    ? 'bg-gray-400 cursor-not-allowed opacity-70'
                    : 'bg-gradient-to-r from-orange-500 to-red-600 hover:from-orange-600 hover:to-red-700'
                }`}
            >
                 {isSpinning ? 'Spinning...' : prizeWonDetails ? 'Prize Claimed!' : fetchingCustomerId ? 'Loading...' : !user ? 'Log in to Spin' : !customerId ? 'Profile Error' : 'SPIN TO WIN!'}
            </button>

            {/* Helper messages */}
            {!user && !errorMessage && <p className="text-gray-500 mt-4 text-sm sm:text-base">Please log in or register!</p>}
            {user && fetchingCustomerId && !errorMessage && <p className="text-gray-500 mt-4 italic text-sm sm:text-base">Loading profile...</p>}
            {user && !customerId && !fetchingCustomerId && !errorMessage && <p className="text-red-600 font-semibold mt-4 text-sm sm:text-base">Profile error. Cannot spin.</p>}
        </div>
    );
};

export default SpinWheelCss;