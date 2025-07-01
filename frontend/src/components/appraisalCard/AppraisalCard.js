import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";
import UserIcon from "../userIcon/UserIcon";
import { handleGetUserAvatar } from "../../handles/handleGetUserAvatar";
import { useTranslation } from "react-i18next";
import AppraisalScoreStarBadge from "../appraisalScoreStarBadge/AppraisalScoreStarBadge";
import AppraisalStateBadge from "../appraisalStateBadge/AppraisalStateBadge";
import "./AppraisalCard.css";
import {
  formatStringToDate,
} from "../../utils/utilityFunctions";

const AppraisalCard = ({ appraisal }) => {
  const { t } = useTranslation();

  return (
    <div className="appraisalCard-container">
      {/* Appraised User */}
      <div className="appraisalCard-user appraisalCard-appraised">
        <UserIcon user={appraisal.appraisedUser} />
        <div className="appraisalCard-userInfo">
          <div className="appraisalCard-name">
            {appraisal.appraisedUser.name} {appraisal.appraisedUser.surname}
          </div>
          <div className="appraisalCard-email">
            {appraisal.appraisedUser.email}
          </div>
        </div>
      </div>

      <AppraisalScoreStarBadge score={appraisal.score} />

      {/* Appraising User */}
      <div className="appraisalCard-user appraisalCard-appraising">
        <UserIcon user={appraisal.appraisingUser} />
        <div className="appraisalCard-userInfo">
          <div className="appraisalCard-name">
            {appraisal.appraisingUser.name} {appraisal.appraisingUser.surname}
          </div>
          <div className="appraisalCard-email">
            {appraisal.appraisingUser.email}
          </div>
        </div>
      </div>

      {/* End Date */}
      <div className="appraisalCard-endDate">
        {formatStringToDate(appraisal.endDate)}
      </div>

      {/* State */}
      <AppraisalStateBadge state={appraisal.state} />
    </div>
  );
};

AppraisalCard.propTypes = {
  appraisal: PropTypes.shape({
    id: PropTypes.number.isRequired,
    feedback: PropTypes.string,
    cycleId: PropTypes.number,
    score: PropTypes.number,
    state: PropTypes.string.isRequired,
    creationDate: PropTypes.array,
    endDate: PropTypes.array,
    appraisedUser: PropTypes.object.isRequired,
    appraisingUser: PropTypes.object.isRequired,
  }).isRequired,
};

export default AppraisalCard;
