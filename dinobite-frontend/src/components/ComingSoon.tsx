import comingSoon from '../assets/comingsoon.png';
import { Link } from 'react-router-dom';

export const ComingSoon: React.FC = () => {
  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100 p-6">
      {/* Coming Soon Image */}
      <img
        src={comingSoon}
        alt="Coming Soon"
        className="w-1/2 md:w-1/4 mb-6"
      />

      {/* Main Text */}
      <h1 className="text-5xl font-extrabold text-orange-600 mb-4 text-center">
        This Restaurant Is Not With Us Right Now
      </h1>

      {/* Subtext */}
      <p className="text-xl text-gray-700 text-center mb-6">
        But Don't Worry, It Might Be! <br />
        How About Exploring Other Options While Waiting?
      </p>

      {/* Optional Button */}
      <Link
        to="/"
        className="inline-block text-center bg-orange-600 text-white font-semibold py-5 px-5 rounded-lg shadow-md hover:bg-orange-500 hover:shadow-lg transition-all duration-300 ease-in-out transform hover:scale-105"
      >
        Checkout Other Options
      </Link>

    </div>
  );
};