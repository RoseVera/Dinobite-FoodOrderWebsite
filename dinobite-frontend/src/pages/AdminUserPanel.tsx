import React, { useEffect, useState } from 'react';
import axios from 'axios';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
  DialogClose
} from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"

const formatKey = (key: string) => {
  return key
    .replace(/([A-Z])/g, " $1")
    .replace(/_/g, " ")
    .replace(/^./, str => str.toUpperCase());
};

const shortenText = (text: string, maxLength = 30) =>
  text.length > maxLength ? text.slice(0, maxLength) + '…' : text;

type UserType = 'ADMIN' | 'CUSTOMER' | 'RESTAURANT' | 'COURIER';

interface User {
  id: number;
  name: string;
  mail: string;
  type: UserType;
}

interface UserDetail {
  [key: string]: any;
}

const AdminUserPanel: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [selectedType, setSelectedType] = useState<string>('ALL');
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>("");
  const [detail, setDetail] = useState<UserDetail | null>(null);
  const [detailModalOpen, setDetailModalOpen] = useState(false);

  const fetchUsers = async (type?: UserType) => {
    setLoading(true);
    setError("");
    try {
      const url = type ? `http://localhost:9090/api/v1/users?type=${type}` : `http://localhost:9090/api/v1/users`;
      const res = await axios.get<User[]>(url);
      setUsers(res.data);
    } catch (err) {
      console.error("User fetch error", err);
      setError("Failed to fetch users.");
    } finally {
      setLoading(false);
    }
  };

    const handleView = async (user: User) => {
    if (user.type === "ADMIN") {
      setDetail({ message: "Admin users do not have additional details." });
      setDetailModalOpen(true);
      return;
    }

    const endpointMap: Record<UserType, string> = {
      CUSTOMER: "customers",
      COURIER: "couriers",
      RESTAURANT: "restaurants",
      ADMIN: "" // Not used
    };

    try {
      const res = await axios.get(`http://localhost:9090/api/v1/${endpointMap[user.type]}/users/${user.id}`);
      setDetail(res.data);
    } catch (err) {
      console.error("Detail fetch error", err);
      setDetail({ message: "Failed to load user details." });
    } finally {
      setDetailModalOpen(true);
    }
  };

  const handleDelete = async (userId: number) => {
    const confirmed = window.confirm("Are you sure you want to delete this user?");
    if (!confirmed) return;

    try {
      await axios.delete(`http://localhost:9090/api/v1/users/${userId}`);
      setUsers(prev => prev.filter(u => u.id !== userId));
    } catch (err) {
      console.error("Delete error", err);
      alert("Failed to delete user.");
    }
  };

  useEffect(() => {
    if (selectedType === 'ALL') {
      fetchUsers();
    } else {
      fetchUsers(selectedType as UserType);
    }
  }, [selectedType]);

  return (
    <div className="p-8">
      <h1 className="text-2xl font-bold mb-6">User Management Panel</h1>

      <div className="flex items-center space-x-4 mb-6">
        <label htmlFor="userType" className="text-sm font-medium">Filter by User Type:</label>
        <select
          id="userType"
          value={selectedType}
          onChange={(e) => setSelectedType(e.target.value)}
          className="border border-gray-300 px-3 py-2 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          <option value="ALL">All</option>
          <option value="CUSTOMER">Customer</option>
          <option value="RESTAURANT">Restaurant</option>
          <option value="COURIER">Courier</option>
          <option value="ADMIN">Admin</option>
        </select>
      </div>

      {loading ? (
        <div className="text-gray-600">Loading users...</div>
      ) : error ? (
        <div className="text-red-500">{error}</div>
      ) : users.length === 0 ? (
        <div className="text-gray-500">No users found.</div>
      ) : (
        <table className="min-w-full bg-white border border-gray-200 rounded shadow-sm">
          <thead>
            <tr className="bg-gray-100 text-left text-sm font-semibold text-gray-700">
              <th className="px-4 py-3 border-b">ID</th>
              <th className="px-4 py-3 border-b">Name</th>
              <th className="px-4 py-3 border-b">Email</th>
              <th className="px-4 py-3 border-b">Type</th>
              <th className="px-4 py-3 border-b text-center">Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.id} className="text-sm text-gray-700 hover:bg-gray-50">
                <td className="px-4 py-2 border-b">{user.id}</td>
                <td className="px-4 py-2 border-b">{user.name}</td>
                <td className="px-4 py-2 border-b">{user.mail}</td>
                <td className="px-4 py-2 border-b">{user.type}</td>
                <td className="px-4 py-2 border-b text-center space-x-2">
                  <button onClick={() => handleView(user)} className="px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600 text-xs">
                    View
                  </button>
                  <button onClick={() => handleDelete(user.id)} className="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600 text-xs">
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
        <Dialog open={detailModalOpen} onOpenChange={setDetailModalOpen}>
        <DialogContent>
            <DialogHeader>
            <DialogTitle>User Details</DialogTitle>
            <DialogDescription>Detailed information about the selected user</DialogDescription>
            </DialogHeader>

            <div className="space-y-2 mt-4">
            {detail && typeof detail === "object" ? (
                Object.entries(detail).map(([key, value]) => (
                <div key={key} className="flex justify-between border-b pb-1">
                    <span className="font-medium text-gray-600">{formatKey(key)}</span>
                    <span
                        className="text-gray-800 max-w-[250px] break-words whitespace-normal"
                        title={String(value)}
                        >
                        {shortenText(String(value))}
                    </span>
                </div>
                ))
            ) : (
                <div className="text-sm text-gray-500">No details available.</div>
            )}
            </div>

            <DialogFooter className="pt-4">
            <DialogClose asChild>
                <Button variant="outline">Close</Button>
            </DialogClose>
            </DialogFooter>
        </DialogContent>
        </Dialog>
    </div>
  );
};

export default AdminUserPanel;
