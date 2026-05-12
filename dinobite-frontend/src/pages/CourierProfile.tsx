import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import '../styles/CourierProfile.css';
import CourierRequests from './CourierRequests';
import CourierActive from './CourierActive';
import CourierPast from './CourierPast';
import axios from 'axios';
type Courier = {
  availability: boolean;
  photo: string;
  userId: number;
  birthDate: string;
  status: string;
};

const CourierProfile: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('requests');
  const [showModal, setShowModal] = useState(false);
  const [courier, setCourier] = useState<Courier>({
    availability: false,
    photo: '',
    userId: 0,
    birthDate: '',
    status: ''
  });

  useEffect(() => {
    const fetchCourier = async () => {
      try {
        const res = await axios.get(`http://localhost:9090/api/v1/couriers/byId/${id}`);
        console.log("kurye ", res.data)
        setCourier(res.data);
      } catch (err) {
        console.error("Courier data couldn't be fetched", err);
      }
    };
    fetchCourier();
  }, [id]);

  const handleUpdate = async () => {
    try {
      await axios.patch(
        `http://localhost:9090/api/v1/couriers/${id}/availability`,
        null,
        {
          params: {
            availability: courier.availability
          }
        }
      );

      await axios.put(`http://localhost:9090/api/v1/couriers/${id}`, {
        userId: courier.userId,
        availability: courier.availability,
        photo: courier.photo,
        birthDate: courier.birthDate,
        status: courier.status
      });
      console.log("status ", courier.status)

      setShowModal(false);
    } catch (err) {
      console.error("Courier couldn't be updated", err);
    }
  };

  const handleDeleteAccount = async () => {
    const confirmed = window.confirm("Are you sure you want to delete your account?");
    if (!confirmed) return;

    try {
      await axios.delete(`http://localhost:9090/api/v1/couriers/${id}`);

      await axios.delete(`http://localhost:9090/api/v1/users/${courier.userId}`);


      alert("Your account has been deleted.");
      navigate('/');
    } catch (error) {
      console.error("Account could not be deleted", error);
      alert("Failed to delete account. Please try again.");
    }
  };


  return (
    <div className="courier-profile">
      <div className="edit-profile-section">
        <button className="edit-button" onClick={() => setShowModal(true)}>
          Edit Info
        </button>
      </div>

      <div className="tab-selector">
        <div className={`tab ${activeTab === 'requests' ? 'active' : ''}`} onClick={() => setActiveTab('requests')}>
          Delivery Requests
        </div>
        <div className={`tab ${activeTab === 'active' ? 'active' : ''}`} onClick={() => setActiveTab('active')}>
          Active Deliveries
        </div>
        <div className={`tab ${activeTab === 'past' ? 'active' : ''}`} onClick={() => setActiveTab('past')}>
          Past Deliveries & Ratings
        </div>
      </div>

      <div className="tab-content">
        {activeTab === 'requests' && <CourierRequests />}
        {activeTab === 'active' && <CourierActive />}
        {activeTab === 'past' && <CourierPast />}
      </div>

      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center">
          <div className="absolute inset-0 bg-black opacity-40"></div>

          {/* Modal */}
          <div className="relative bg-white p-6 rounded-2xl shadow-2xl w-full max-w-md border border-gray-300 z-10">
            <h2 className="text-lg font-bold mb-4">Edit Profile</h2>

            <label className="block mb-2 text-sm font-medium">Status:</label>
            <select
              className="w-full border border-gray-300 px-3 py-2 rounded mb-4 focus:outline-none focus:ring-2 focus:ring-blue-400"
              value={courier.status}
              onChange={(e) => setCourier({ ...courier, status: e.target.value })}
            >
              <option value="ACTIVE">ACTIVE</option>
              <option value="INACTIVE">INACTIVE</option>
              <option value="ON_DELIVERY">ON_DELIVERY</option>
            </select>

            <label className="block mb-2 text-sm font-medium">Availability:</label>
            <div
              className={`w-14 h-8 flex items-center rounded-full p-1 cursor-pointer transition-colors duration-300 mb-4 ${courier.availability ? 'bg-green-500' : 'bg-gray-400'
                }`}
              onClick={() => setCourier({ ...courier, availability: !courier.availability })}
            >
              <div
                className={`bg-white w-6 h-6 rounded-full shadow-md transform transition-transform duration-300 ${courier.availability ? 'translate-x-6' : 'translate-x-0'
                  }`}
              ></div>
            </div>

            {courier.photo && (
              <div className="flex justify-center mb-4">
                <img
                  src={courier.photo}
                  alt="Courier"
                  className="w-24 h-24 object-cover rounded-full border border-gray-300 shadow-sm"
                />
              </div>
            )}

            <label className="block mb-2 text-sm font-medium">Profile Photo URL:</label>
            <input
              className="w-full border border-gray-300 px-3 py-2 rounded mb-4 focus:outline-none focus:ring-2 focus:ring-blue-400"
              type="text"
              value={courier.photo}
              onChange={(e) => setCourier({ ...courier, photo: e.target.value })}
            />

            <hr className="my-4" />

            <button
                className="bg-red-500 text-black px-4 py-2 rounded hover:bg-red-400 transition"
                onClick={handleDeleteAccount}
            >
              Delete My Account
            </button>


            <div className="flex justify-end space-x-2">
              <button
                className="bg-gray-300 text-black px-4 py-2 rounded hover:bg-gray-400 transition"
                onClick={() => setShowModal(false)}
              >
                Cancel
              </button>
              <button
                className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition"
                onClick={handleUpdate}
              >
                Save
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CourierProfile;
