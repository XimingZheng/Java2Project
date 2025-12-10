import React from 'react';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  Cell,
} from 'recharts';
import type { CoOccurrenceResponse } from '../types/api';

interface CoOccurrenceChartProps {
  data: CoOccurrenceResponse;
}

// Color palette for bars
const COLORS = [
  '#F48024', // StackOverflow orange
  '#0077CC', // StackOverflow blue
  '#5EBA7D', // Green
  '#C73A63', // Red
  '#9147FF', // Purple
  '#FFA500', // Orange
  '#FF6B6B', // Light red
  '#4ECDC4', // Teal
  '#95E1D3', // Mint
  '#F38181', // Pink
];

// Custom tooltip component (outside render)
const CustomTooltip = ({ active, payload }: { active?: boolean; payload?: Array<{ payload: { name: string; count: number; topic1: string; topic2: string } }> }) => {
  if (active && payload && payload.length) {
    const data = payload[0].payload;
    return (
      <div className="bg-white p-4 rounded-lg shadow-lg border border-gray-200">
        <p className="font-semibold text-gray-800 mb-2">Topic Pair</p>
        <p className="text-sm text-gray-600">
          <span className="font-medium text-stackoverflow-orange">{data.topic1}</span>
          {' + '}
          <span className="font-medium text-stackoverflow-blue">{data.topic2}</span>
        </p>
        <p className="text-sm text-gray-700 mt-2">
          Co-occurrences: <span className="font-bold">{data.count}</span>
        </p>
      </div>
    );
  }
  return null;
};

const CoOccurrenceChart: React.FC<CoOccurrenceChartProps> = ({ data }) => {
  // Transform data for chart
  const chartData = data.coOccurrences.map((pair) => ({
    name: `${pair.topic1} + ${pair.topic2}`,
    count: pair.count,
    topic1: pair.topic1,
    topic2: pair.topic2,
  }));

  return (
    <div className="w-full">
      <div className="mb-4 p-4 bg-gray-50 rounded-lg">
        <p className="text-sm text-gray-600">
          Showing top <span className="font-semibold">{data.topN}</span> topic pairs out of{' '}
          <span className="font-semibold">{data.totalPairs}</span> total pairs
        </p>
      </div>

      <ResponsiveContainer width="100%" height={600}>
        <BarChart
          data={chartData}
          margin={{ top: 70, right: 30, left: 20, bottom: 70 }}
        >
          <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
          <XAxis
            dataKey="name"
            angle={-45}
            textAnchor="end"
            height={120}
            tick={{ fontSize: 12 }}
            interval={0}
          />
          <YAxis
            label={{ value: 'Co-occurrence Count', angle: -90, position: 'insideLeft' }}
            tick={{ fontSize: 12 }}
          />
          <Tooltip content={<CustomTooltip />} />
          <Legend wrapperStyle={{ paddingTop: '20px' }} />
          <Bar dataKey="count" name="Co-occurrences" radius={[8, 8, 0, 0]}>
            {chartData.map((_entry, index) => (
              <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
            ))}
          </Bar>
        </BarChart>
      </ResponsiveContainer>

      <div className="mt-6 p-4 bg-blue-50 rounded-lg border border-blue-200">
        <h4 className="font-semibold text-blue-900 mb-2">Insights</h4>
        <p className="text-sm text-blue-800">
          Topic co-occurrences reveal cross-cutting challenges that developers face when combining multiple technologies.
          Higher co-occurrence counts indicate common problem areas that require expertise in both topics.
        </p>
      </div>
    </div>
  );
};

export default CoOccurrenceChart;
