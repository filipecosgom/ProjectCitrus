import React, { useState, useEffect } from "react";
import { IoCalendar, IoAdd, IoClose, IoCheckmark } from "react-icons/io5";
import "./Cycles.css";

const Cycles = () => {
  const [cycles, setCycles] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // TODO: Fetch cycles from API
    setLoading(false);
  }, []);

  return (
    <div className="cycles-page">
      <div className="cycles-header">
        <div className="cycles-title">
          <IoCalendar className="cycles-icon" />
          <h1>Cycle Management</h1>
        </div>
        <button className="btn-create-cycle">
          <IoAdd /> Create New Cycle
        </button>
      </div>

      <div className="cycles-content">
        <div className="cycles-grid">
          {/* Placeholder content */}
          <div className="cycle-card">
            <div className="cycle-header">
              <h3>2024 Q2 Performance Cycle</h3>
              <span className="cycle-status active">OPEN</span>
            </div>
            <div className="cycle-body">
              <p><strong>Start:</strong> April 1, 2024</p>
              <p><strong>End:</strong> June 30, 2024</p>
              <p><strong>Participants:</strong> 25 users</p>
            </div>
            <div className="cycle-actions">
              <button className="btn-cycle-action">View Details</button>
              <button className="btn-cycle-action close">Close Cycle</button>
            </div>
          </div>

          <div className="cycle-card">
            <div className="cycle-header">
              <h3>2024 Q1 Performance Cycle</h3>
              <span className="cycle-status closed">CLOSED</span>
            </div>
            <div className="cycle-body">
              <p><strong>Start:</strong> January 1, 2024</p>
              <p><strong>End:</strong> March 31, 2024</p>
              <p><strong>Participants:</strong> 22 users</p>
            </div>
            <div className="cycle-actions">
              <button className="btn-cycle-action">View Report</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Cycles;