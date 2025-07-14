/**
 * AppraisalScoreStarBadge module.
 * Renders a star badge to visually represent an appraisal score (0-4 stars).
 * Applies color logic for each score and handles no-score state.
 * @module AppraisalScoreStarBadge
 */
import { FaStar } from "react-icons/fa6";
import "./AppraisalScoreStarBadge.css";

/**
 * ScoreStarCard component for displaying a star badge for appraisal scores.
 * @param {Object} props - Component props
 * @param {number|null} [props.score=null] - Appraisal score (0-4), or null for no score
 * @param {number} [props.max=4] - Maximum number of stars to display
 * @returns {JSX.Element} The rendered star badge
 */
const ScoreStarCard = ({ score = null, max = 4 }) => {
  // Render a message if no score is provided
  if (score === null) {
    return (
      <div className="storeStarCard-container no-score">No score given</div>
    );
  }
  /**
   * Determines the CSS class for the given score.
   * @param {number} score - Appraisal score (0-4)
   * @returns {string} CSS class name for star coloring
   */
  let starsClass = "";
  if (score === 0) starsClass = "stars-0";
  else if (score === 1) starsClass = "stars-1";
  else if (score === 2) starsClass = "stars-2";
  else if (score === 3) starsClass = "stars-3";
  else if (score === 4) starsClass = "stars-4";

  /**
   * Renders the star icons for the badge.
   * @returns {JSX.Element[]} Array of star icon elements
   */
  return (
    <div className={`storeStarCard-container ${starsClass}`}>
      {[...Array(max)].map((_, i) => (
        <FaStar key={i} className="star" size={20} />
      ))}
    </div>
  );
};

export default ScoreStarCard;
