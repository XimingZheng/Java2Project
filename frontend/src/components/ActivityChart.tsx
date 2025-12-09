import React from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import type { TopicActivityResponse } from '../types/api';

interface ActivityChartProps {
  data: TopicActivityResponse;
}

const ActivityChart: React.FC<ActivityChartProps> = ({ data }) => {
  if (!data || !data.topicActivityScore) return null;

  const topics = Object.keys(data.topicActivityScore);
  const allPeriods = new Set<string>();
  
  topics.forEach(topic => {
    data.topicActivityScore[topic].forEach(item => {
      allPeriods.add(item.period);
    });
  });

  const chartData = Array.from(allPeriods).sort().map(period => {
    const dataPoint: any = { period };
    topics.forEach(topic => {
      const item = data.topicActivityScore[topic].find(d => d.period === period);
      dataPoint[topic] = item ? item.activityScore.toFixed(1) : 0;
    });
    return dataPoint;
  });

  const colors = [
    '#F48024',
    '#0077CC',
    '#5eba7d',
    '#e74c3c',
    '#9b59b6',
    '#f39c12',
    '#1abc9c',
    '#34495e',
    '#e91e63',
  ];

  return (
    <ResponsiveContainer width="100%" height={400}>
      <LineChart data={chartData} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis 
          dataKey="period" 
          angle={-45}
          textAnchor="end"
          height={80}
        />
        <YAxis />
        <Tooltip />
        <Legend />
        {topics.map((topic, index) => (
          <Line
            key={topic}
            type="monotone"
            dataKey={topic}
            stroke={colors[index % colors.length]}
            strokeWidth={2}
            dot={{ r: 4 }}
            activeDot={{ r: 6 }}
          />
        ))}
      </LineChart>
    </ResponsiveContainer>
  );
};

export default ActivityChart;
