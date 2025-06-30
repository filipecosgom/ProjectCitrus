import { FaStar } from "react-icons/fa6";
import "./AppraisalScoreStarBadge.css";

const ScoreStarCard = ({ score = null, max = 4 }) => {
  if (score === null) {
    return <div className="storeStarCard-container no-score">No score given</div>;
  }
  let starsClass = "";
  if (score === 0) starsClass = "stars-0";
  else if (score === 1) starsClass = "stars-1";
  else if (score === 2) starsClass = "stars-2";
  else if (score === 3) starsClass = "stars-3";
  else if (score === 4) starsClass = "stars-4";

  return (
    <div className={`storeStarCard-container ${starsClass}`}>
      {[...Array(max)].map((_, i) => (
        <FaStar key={i} className="star" size={20} />
      ))}
    </div>
  );
};

export default ScoreStarCard;
