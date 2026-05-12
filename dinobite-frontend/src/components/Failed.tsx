import ErrorImg from '../assets/loading.png';

interface ErrorPageProps {
    error: string; // Type of the error message
  }
  
  export const ErrorPage: React.FC<ErrorPageProps> = ({ error }) => {
    return (
      <div className="flex flex-col items-center justify-center h-screen bg-gray-100">
        <h1 className="text-6xl font-bold text-red-500 mb-4">Something Went Wrong</h1>
        <img
          src={ErrorImg}
          className="w-96"
          alt="Error"
        />
        <p className="text-gray-600 mt-4 text-3xl">{error}</p>
      </div>
    );
  };
  