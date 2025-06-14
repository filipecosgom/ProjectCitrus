

const { remainingTime } = useAuthStore();

const formatTime = (ms) => {
  const minutes = Math.floor(ms / 60000);
  const seconds = Math.floor((ms % 60000) / 1000);
  return `${minutes}:${seconds < 10 ? "0" : ""}${seconds}`;
};

return <p>Session expires in: {formatTime(remainingTime)}</p>;