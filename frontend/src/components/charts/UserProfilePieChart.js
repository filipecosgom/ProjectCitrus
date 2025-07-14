import { PieChart, Pie, Cell, Tooltip, Legend } from "recharts";

export default function UserProfilePieChart({ complete, incomplete, t }) {
  const data = [
    { name: t("users.accountStateComplete"), value: complete },
    { name: t("users.accountStateIncomplete"), value: incomplete },
  ];
  const COLORS = ["#9C2F31", "#FF8042"];

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
        {t("dashboard.users")}
      </h3>
      <PieChart width={264} height={220}>
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
