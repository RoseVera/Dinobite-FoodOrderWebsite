import loadingImg from '../assets/loading.png';

export const LoadingPage: React.FC = () => {
  return (
    <div className="flex flex-col items-center justify-center h-screen bg-gray-100">
      <h1 className="text-6xl font-bold text-orange-600 mb-16 animate-pulse">
        Loading...
      </h1>
      <img
        src={loadingImg}
        className="w-48 h-48 animate-bounce"
        alt="Loading Dinosaur"
      />
    </div>
  );
};