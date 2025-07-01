import React from "react";
import PropTypes from "prop-types";
import UserIcon from "../userIcon/UserIcon";
import { useTranslation } from "react-i18next";
import AppraisalScoreStarBadge from "../appraisalScoreStarBadge/AppraisalScoreStarBadge";
import AppraisalStateBadge from "../appraisalStateBadge/AppraisalStateBadge";
import "./AppraisalCard.css";
import { formatStringToDate } from "../../utils/utilityFunctions";

const AppraisalCard = ({ appraisal }) => {
  const { t } = useTranslation();

  return (
    <div className="appraisalCard-container">
      {/* ✅ COLUNA 1: Appraised User */}
      <div
        className="appraisalCard-user appraisalCard-appraised"
        data-label={t("user")} // Para mobile
      >
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

      {/* ✅ COLUNA 2: Score */}
      <div
        className="appraisalCard-score"
        data-label={t("score")} // Para mobile
      >
        <AppraisalScoreStarBadge score={appraisal.score} />
      </div>

      {/* ✅ COLUNA 3: Appraising User (Manager) */}
      <div
        className="appraisalCard-user appraisalCard-appraising"
        data-label={t("manager")} // Para mobile
      >
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

      {/* ✅ COLUNA 4: End Date */}
      <div
        className="appraisalCard-endDate"
        data-label={t("endDate")} // Para mobile
      >
        {formatStringToDate(appraisal.endDate)}
      </div>

      {/* ✅ COLUNA 5: State */}
      <div
        className="appraisalCard-state"
        data-label={t("state")} // Para mobile
      >
        <AppraisalStateBadge state={appraisal.state} />
      </div>
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
