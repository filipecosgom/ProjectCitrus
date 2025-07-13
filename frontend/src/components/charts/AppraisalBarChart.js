import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";

export default function AppraisalBarChart({
  inProgress,
  completed,
  closed,
  t,
}) {
  const data = [
    { name: t("appraisalStateInProgress"), value: inProgress },
    { name: t("appraisalStateCompleted"), value: completed },
    { name: t("appraisalStateClosed"), value: closed },
  ];

  return (
    <div
      style={{
        background: "#fff",
        borderRadius: 10,
        padding: 24,
        boxShadow: "0 4px 16px rgba(0,0,0,0.08)",
        minWidth: 260,
      }}
    >
      <h3 style={{ textAlign: "center", marginBottom: 12 }}>
        {t("dashboard.appraisals")}
      </h3>
      <ResponsiveContainer width="100%" height={220}>
        <BarChart data={data}>
          <XAxis dataKey="name" />
          <YAxis allowDecimals={false} />
          <Tooltip />
          <Legend />
          <Bar dataKey="value" fill="#9C2F31" />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}
