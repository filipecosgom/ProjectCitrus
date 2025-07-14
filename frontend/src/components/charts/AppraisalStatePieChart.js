import { PieChart, Pie, Cell, Tooltip, Legend } from "recharts";

export default function AppraisalStatePieChart({
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
  const COLORS = ["#E57373", "#FF8042", "#9C2F31"];

  return (
    <div
      style={{
        background: "#fff",
        borderRadius: 10,
        padding: 24,
        boxShadow: "0 4px 16px rgba(0,0,0,0.08)",
      }}
    >
      <h3 style={{ textAlign: "center", marginBottom: 12 }}>
        {t("dashboard.appraisalsDistribution")}
      </h3>
      <PieChart width={220} height={220}>
        <Pie
          data={data}
          cx="50%"
          cy="50%"
          innerRadius={60}
          outerRadius={90}
          fill="#9C2F31"
          paddingAngle={5}
          dataKey="value"
          startAngle={90}
          endAngle={450}
        >
          {data.map((entry, index) => (
            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
          ))}
        </Pie>
        <Tooltip />
        <Legend />
      </PieChart>
    </div>
  );
}
