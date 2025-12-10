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
import type { MultithreadingResponse } from '../types/api';

interface MultithreadingChartProps {
  data: MultithreadingResponse;
}

// Category colors mapping
const COLOR_PALETTE = [
  '#F48024', // orange
  '#0077CC', // blue
  '#5EBA7D', // green
  '#C73A63', // pink
  '#9147FF', // purple
  '#FFD93D', // yellow
  '#FFA500', // amber
  '#4ECDC4', // cyan
  '#7B68EE', // medium slate blue
  '#6BCF7F', // lime
  '#FF85A2', // rose
  '#FF6B6B', // red
];

// Function to get color for a category
const getCategoryColor = (category: string): string => {
  // Use hash of category name to consistently assign colors
  const hash = category.split('').reduce((acc, char) => acc + char.charCodeAt(0), 0);
  return COLOR_PALETTE[hash % COLOR_PALETTE.length];
};

// Custom tooltip component (outside render)
const CustomTooltip = ({ active, payload }: { active?: boolean; payload?: Array<{ payload: { name: string; count: number; category: string; color: string } }> }) => {
  if (active && payload && payload.length) {
    const data = payload[0].payload;
    return (
      <div className="bg-white p-4 rounded-lg shadow-lg border border-gray-200">
        <p className="font-semibold text-gray-800 mb-1">{data.name}</p>
        <p className="text-xs text-gray-500 mb-2">
          Category: <span className="font-medium">{data.category}</span>
        </p>
        <p className="text-sm text-gray-700">
          Occurrences: <span className="font-bold text-stackoverflow-orange">{data.count}</span>
        </p>
      </div>
    );
  }
  return null;
};

const MultithreadingChart: React.FC<MultithreadingChartProps> = ({ data }) => {
  // Get unique categories for legend and create color mapping
  const categories = Array.from(new Set(data.topProblems.map(p => p.category)));
  const categoryColorMap: { [key: string]: string } = {};
  categories.forEach((cat) => {
    categoryColorMap[cat] = getCategoryColor(cat);
  });

  // Transform data for chart
  const chartData = data.topProblems.map((problem) => ({
    name: problem.patternName,
    count: problem.count,
    category: problem.category,
    color: categoryColorMap[problem.category],
  }));

  return (
    <div className="w-full">
      <div className="mb-4 p-4 bg-gray-50 rounded-lg">
        <p className="text-sm text-gray-600">
          Analyzed <span className="font-semibold">{data.totalThreads}</span> multithreading-related threads
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
            tick={{ fontSize: 11 }}
            interval={0}
          />
          <YAxis
            label={{ value: 'Occurrence Count', angle: -90, position: 'insideLeft' }}
            tick={{ fontSize: 12 }}
          />
          <Tooltip content={<CustomTooltip />} />
          <Legend wrapperStyle={{ paddingTop: '20px' }} />
          <Bar dataKey="count" name="Occurrences" radius={[8, 8, 0, 0]}>
            {chartData.map((entry, index) => (
              <Cell key={`cell-${index}`} fill={entry.color} />
            ))}
          </Bar>
        </BarChart>
      </ResponsiveContainer>

      {/* Category legend */}
      <div className="mt-6 flex flex-wrap gap-3 justify-center">
        {categories.map((category) => (
          <div key={category} className="flex items-center gap-2">
            <div
              className="w-4 h-4 rounded"
              style={{ backgroundColor: categoryColorMap[category] }}
            />
            <span className="text-sm text-gray-700 capitalize">{category.replace(/-|_/g, ' ')}</span>
          </div>
        ))}
      </div>

      <div className="mt-6 p-4 bg-yellow-50 rounded-lg border border-yellow-200">
        <h4 className="font-semibold text-yellow-900 mb-2">Common Pitfalls</h4>
        <p className="text-sm text-yellow-800">
          These patterns represent the most frequently encountered problems in Java multithreading.
          Understanding these pitfalls is crucial for writing thread-safe and efficient concurrent code.
        </p>
      </div>

      {/* Top problem highlights */}
      {data.topProblems.length > 0 && (
        <div className="mt-4 grid grid-cols-1 md:grid-cols-3 gap-4">
          {data.topProblems.slice(0, 3).map((problem, index) => (
            <div
              key={problem.patternName}
              className="p-4 rounded-lg border-2"
              style={{ borderColor: categoryColorMap[problem.category] }}
            >
              <div className="flex items-center gap-2 mb-2">
                <span className="text-2xl font-bold text-gray-400">#{index + 1}</span>
                <span className="text-xs px-2 py-1 rounded bg-gray-100 text-gray-600">
                  {problem.category}
                </span>
              </div>
              <p className="text-sm font-semibold text-gray-800 mb-1">
                {problem.patternName}
              </p>
              <p className="text-xs text-gray-600">
                {problem.count} occurrences
              </p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default MultithreadingChart;
