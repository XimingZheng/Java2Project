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
  RadarChart,
  PolarGrid,
  PolarAngleAxis,
  PolarRadiusAxis,
  Radar,
} from 'recharts';
import type { SolvableAnalysisResponse } from '../types/api';

interface SolvableAnalysisChartProps {
  data: SolvableAnalysisResponse;
}

// Custom tooltip component
const CustomTooltip = ({ active, payload }: { active?: boolean; payload?: Array<{ name: string; value: number; color: string }> }) => {
  if (active && payload && payload.length) {
    return (
      <div className="bg-white p-4 rounded-lg shadow-lg border border-gray-200">
        {payload.map((entry, index) => (
          <p key={index} className="text-sm" style={{ color: entry.color }}>
            <span className="font-semibold">{entry.name}:</span> {typeof entry.value === 'number' ? entry.value.toFixed(2) : entry.value}
          </p>
        ))}
      </div>
    );
  }
  return null;
};

const SolvableAnalysisChart: React.FC<SolvableAnalysisChartProps> = ({ data }) => {
  // Prepare comparison data for bar charts
  const reputationData = [
    { name: 'User Reputation', solvable: data.reputationAnalysis.solvableAvg, notSolvable: data.reputationAnalysis.notSolvableAvg }
  ];

  const questionLengthData = [
    { name: 'Characters', solvable: data.questionLengthAnalysis.solvableAvgCharacters, notSolvable: data.questionLengthAnalysis.notSolvableAvgCharacters },
    { name: 'Words', solvable: data.questionLengthAnalysis.solvableAvgWords, notSolvable: data.questionLengthAnalysis.notSolvableAvgWords }
  ];

  const codeSnippetData = [
    { name: 'With Code %', solvable: data.codeSnippetAnalysis.solvableWithCodePercentage, notSolvable: data.codeSnippetAnalysis.notSolvableWithCodePercentage },
    { name: 'Avg Code Blocks', solvable: data.codeSnippetAnalysis.solvableAvgCodeBlocks, notSolvable: data.codeSnippetAnalysis.notSolvableAvgCodeBlocks }
  ];

  const otherFactorsData = [
    { name: 'Avg Tags', solvable: data.tagCountAnalysis.solvableAvgTags, notSolvable: data.tagCountAnalysis.notSolvableAvgTags },
    { name: 'Question Score', solvable: data.questionScoreAnalysis.solvableAvgScore, notSolvable: data.questionScoreAnalysis.notSolvableAvgScore },
    { name: 'View Count', solvable: data.viewCountAnalysis.solvableAvgViews! / 100, notSolvable: data.viewCountAnalysis.notSolvableAvgViews! / 100 }, // Scaled down
  ];

  const titleLengthData = [
    { name: 'Title Characters', solvable: data.titleLengthAnalysis.solvableAvgCharacters, notSolvable: data.titleLengthAnalysis.notSolvableAvgCharacters },
    { name: 'Title Words', solvable: data.titleLengthAnalysis.solvableAvgWords, notSolvable: data.titleLengthAnalysis.notSolvableAvgWords }
  ];

  // Prepare radar chart data - normalized scores
  const radarData = [
    {
      factor: 'Reputation',
      solvable: Math.min(100, (data.reputationAnalysis.solvableAvg || 0) / 100),
      notSolvable: Math.min(100, (data.reputationAnalysis.notSolvableAvg || 0) / 100),
    },
    {
      factor: 'Question Length',
      solvable: Math.min(100, (data.questionLengthAnalysis.solvableAvgWords || 0) / 4),
      notSolvable: Math.min(100, (data.questionLengthAnalysis.notSolvableAvgWords || 0) / 4),
    },
    {
      factor: 'Code Snippets',
      solvable: data.codeSnippetAnalysis.solvableWithCodePercentage || 0,
      notSolvable: data.codeSnippetAnalysis.notSolvableWithCodePercentage || 0,
    },
    {
      factor: 'Tags',
      solvable: Math.min(100, (data.tagCountAnalysis.solvableAvgTags || 0) * 20),
      notSolvable: Math.min(100, (data.tagCountAnalysis.notSolvableAvgTags || 0) * 20),
    },
    {
      factor: 'Question Score',
      solvable: Math.min(100, Math.max(0, (data.questionScoreAnalysis.solvableAvgScore || 0) * 10 + 50)),
      notSolvable: Math.min(100, Math.max(0, (data.questionScoreAnalysis.notSolvableAvgScore || 0) * 10 + 50)),
    },
    {
      factor: 'View Count',
      solvable: Math.min(100, (data.viewCountAnalysis.solvableAvgViews || 0) * 0.5),
      notSolvable: Math.min(100, (data.viewCountAnalysis.notSolvableAvgViews || 0) * 0.5),
    },
  ];

  return (
    <div className="w-full space-y-6">
      {/* Basic Statistics */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div className="bg-green-50 p-4 rounded-lg border-2 border-green-200">
          <p className="text-sm text-gray-600 mb-1">Solvable Questions</p>
          <p className="text-2xl font-bold text-green-600">{data.basicStats.totalSolvable.toLocaleString()}</p>
          <p className="text-xs text-gray-500 mt-1">{data.basicStats.solvablePercentage.toFixed(1)}%</p>
        </div>
        <div className="bg-red-50 p-4 rounded-lg border-2 border-red-200">
          <p className="text-sm text-gray-600 mb-1">Hard-to-Solve Questions</p>
          <p className="text-2xl font-bold text-red-600">{data.basicStats.totalNotSolvable.toLocaleString()}</p>
          <p className="text-xs text-gray-500 mt-1">{data.basicStats.notSolvablePercentage.toFixed(1)}%</p>
        </div>
        <div className="bg-blue-50 p-4 rounded-lg border-2 border-blue-200">
          <p className="text-sm text-gray-600 mb-1">Total Questions</p>
          <p className="text-2xl font-bold text-blue-600">{data.basicStats.totalQuestions.toLocaleString()}</p>
          <p className="text-xs text-gray-500 mt-1">Analyzed</p>
        </div>
        <div className="bg-purple-50 p-4 rounded-lg border-2 border-purple-200">
          <p className="text-sm text-gray-600 mb-1">Response Time</p>
          <p className="text-2xl font-bold text-purple-600">{data.responseTimeAnalysis.solvableAvgResponseHours?.toFixed(1)}h</p>
          <p className="text-xs text-gray-500 mt-1">Avg for solvable</p>
        </div>
      </div>

      {/* Radar Chart Overview */}
      <div className="bg-white p-6 rounded-lg shadow-md">
        <h3 className="text-lg font-semibold mb-4 text-gray-800">Factor Comparison Overview</h3>
        <ResponsiveContainer width="100%" height={400}>
          <RadarChart data={radarData}>
            <PolarGrid stroke="#e0e0e0" />
            <PolarAngleAxis dataKey="factor" tick={{ fontSize: 12 }} />
            <PolarRadiusAxis angle={90} domain={[0, 100]} tick={{ fontSize: 10 }} />
            <Radar name="Solvable" dataKey="solvable" stroke="#5EBA7D" fill="#5EBA7D" fillOpacity={0.6} />
            <Radar name="Hard-to-Solve" dataKey="notSolvable" stroke="#FF6B6B" fill="#FF6B6B" fillOpacity={0.6} />
            <Legend />
            <Tooltip />
          </RadarChart>
        </ResponsiveContainer>
      </div>

      {/* Factor 1: User Reputation */}
      <div className="bg-white p-6 rounded-lg shadow-md">
        <h3 className="text-lg font-semibold mb-2 text-gray-800">Factor 1: User Reputation</h3>
        <p className="text-sm text-gray-600 mb-4">
          Higher reputation users tend to ask more solvable questions
        </p>
        <ResponsiveContainer width="100%" height={250}>
          <BarChart data={reputationData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
            <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
            <XAxis dataKey="name" tick={{ fontSize: 12 }} />
            <YAxis tick={{ fontSize: 12 }} />
            <Tooltip content={<CustomTooltip />} />
            <Legend />
            <Bar dataKey="solvable" name="Solvable" fill="#5EBA7D" radius={[8, 8, 0, 0]} />
            <Bar dataKey="notSolvable" name="Hard-to-Solve" fill="#FF6B6B" radius={[8, 8, 0, 0]} />
          </BarChart>
        </ResponsiveContainer>
        <div className="mt-3 p-3 bg-blue-50 rounded">
          <p className="text-sm text-gray-700">
            <span className="font-semibold">Difference:</span> {data.reputationAnalysis.difference?.toFixed(2)} reputation points
          </p>
        </div>
      </div>

      {/* Factor 2: Question Length & Clarity */}
      <div className="bg-white p-6 rounded-lg shadow-md">
        <h3 className="text-lg font-semibold mb-2 text-gray-800">Factor 2: Question Length & Clarity</h3>
        <p className="text-sm text-gray-600 mb-4">
          Longer, more detailed questions receive better answers
        </p>
        <ResponsiveContainer width="100%" height={300}>
          <BarChart data={questionLengthData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
            <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
            <XAxis dataKey="name" tick={{ fontSize: 12 }} />
            <YAxis tick={{ fontSize: 12 }} />
            <Tooltip content={<CustomTooltip />} />
            <Legend />
            <Bar dataKey="solvable" name="Solvable" fill="#5EBA7D" radius={[8, 8, 0, 0]} />
            <Bar dataKey="notSolvable" name="Hard-to-Solve" fill="#FF6B6B" radius={[8, 8, 0, 0]} />
          </BarChart>
        </ResponsiveContainer>
        <div className="mt-3 p-3 bg-blue-50 rounded">
          <p className="text-sm text-gray-700">
            <span className="font-semibold">Character Difference:</span> {data.questionLengthAnalysis.characterDifference?.toFixed(2)} chars
            <span className="ml-4 font-semibold">Word Difference:</span> {data.questionLengthAnalysis.wordDifference?.toFixed(2)} words
          </p>
        </div>
      </div>

      {/* Factor 3: Code Snippets */}
      <div className="bg-white p-6 rounded-lg shadow-md">
        <h3 className="text-lg font-semibold mb-2 text-gray-800">Factor 3: Code Snippet Presence</h3>
        <p className="text-sm text-gray-600 mb-4">
          Questions with code examples are more likely to be solved
        </p>
        <ResponsiveContainer width="100%" height={300}>
          <BarChart data={codeSnippetData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
            <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
            <XAxis dataKey="name" tick={{ fontSize: 12 }} />
            <YAxis tick={{ fontSize: 12 }} />
            <Tooltip content={<CustomTooltip />} />
            <Legend />
            <Bar dataKey="solvable" name="Solvable" fill="#5EBA7D" radius={[8, 8, 0, 0]} />
            <Bar dataKey="notSolvable" name="Hard-to-Solve" fill="#FF6B6B" radius={[8, 8, 0, 0]} />
          </BarChart>
        </ResponsiveContainer>
        <div className="mt-3 p-3 bg-blue-50 rounded">
          <p className="text-sm text-gray-700">
            <span className="font-semibold">Percentage Difference:</span> {data.codeSnippetAnalysis.percentageDifference?.toFixed(2)}% more solvable questions have code
          </p>
        </div>
      </div>

      {/* Additional Factors Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Title Length */}
        <div className="bg-white p-6 rounded-lg shadow-md">
          <h3 className="text-lg font-semibold mb-2 text-gray-800">Title Length</h3>
          <ResponsiveContainer width="100%" height={250}>
            <BarChart data={titleLengthData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
              <XAxis dataKey="name" tick={{ fontSize: 11 }} />
              <YAxis tick={{ fontSize: 11 }} />
              <Tooltip content={<CustomTooltip />} />
              <Legend />
              <Bar dataKey="solvable" name="Solvable" fill="#5EBA7D" radius={[8, 8, 0, 0]} />
              <Bar dataKey="notSolvable" name="Hard-to-Solve" fill="#FF6B6B" radius={[8, 8, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Other Factors */}
        <div className="bg-white p-6 rounded-lg shadow-md">
          <h3 className="text-lg font-semibold mb-2 text-gray-800">Other Factors</h3>
          <ResponsiveContainer width="100%" height={250}>
            <BarChart data={otherFactorsData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
              <XAxis dataKey="name" tick={{ fontSize: 11 }} />
              <YAxis tick={{ fontSize: 11 }} />
              <Tooltip content={<CustomTooltip />} />
              <Legend />
              <Bar dataKey="solvable" name="Solvable" fill="#5EBA7D" radius={[8, 8, 0, 0]} />
              <Bar dataKey="notSolvable" name="Hard-to-Solve" fill="#FF6B6B" radius={[8, 8, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Key Insights */}
      <div className="bg-gradient-to-r from-green-50 to-blue-50 p-6 rounded-lg border-2 border-green-200">
        <h3 className="text-lg font-semibold mb-3 text-gray-800">Key Insights</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="bg-white p-4 rounded-lg">
            <h4 className="font-semibold text-green-600 mb-2">Solvable Characteristics</h4>
            <ul className="text-sm text-gray-700 space-y-1">
              <li>• Higher user reputation ({data.reputationAnalysis.solvableAvg?.toFixed(0)})</li>
              <li>• Longer questions ({data.questionLengthAnalysis.solvableAvgWords?.toFixed(0)} words)</li>
              <li>• More code snippets ({data.codeSnippetAnalysis.solvableWithCodePercentage?.toFixed(1)}%)</li>
              <li>• More tags ({data.tagCountAnalysis.solvableAvgTags?.toFixed(1)})</li>
              <li>• Higher view count ({data.viewCountAnalysis.solvableAvgViews?.toFixed(0)})</li>
            </ul>
          </div>
          <div className="bg-white p-4 rounded-lg">
            <h4 className="font-semibold text-red-600 mb-2">Hard-to-Solve Characteristics</h4>
            <ul className="text-sm text-gray-700 space-y-1">
              <li>• Lower user reputation ({data.reputationAnalysis.notSolvableAvg?.toFixed(0)})</li>
              <li>• Shorter questions ({data.questionLengthAnalysis.notSolvableAvgWords?.toFixed(0)} words)</li>
              <li>• Fewer code snippets ({data.codeSnippetAnalysis.notSolvableWithCodePercentage?.toFixed(1)}%)</li>
              <li>• {data.responseTimeAnalysis.notSolvableNoAnswerPercentage?.toFixed(1)}% have no answers</li>
              <li>• Lower engagement scores</li>
            </ul>
          </div>
        </div>
      </div>

      {/* Response Time Analysis */}
      <div className="bg-white p-6 rounded-lg shadow-md">
        <h3 className="text-lg font-semibold mb-2 text-gray-800">Response Time Analysis</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mt-4">
          <div className="p-4 bg-green-50 rounded-lg">
            <p className="text-sm text-gray-600">Solvable Avg Response</p>
            <p className="text-2xl font-bold text-green-600">{data.responseTimeAnalysis.solvableAvgResponseHours?.toFixed(1)}h</p>
          </div>
          <div className="p-4 bg-orange-50 rounded-lg">
            <p className="text-sm text-gray-600">Hard-to-Solve Avg Response</p>
            <p className="text-2xl font-bold text-orange-600">{data.responseTimeAnalysis.notSolvableAvgResponseHours?.toFixed(1)}h</p>
          </div>
          <div className="p-4 bg-red-50 rounded-lg">
            <p className="text-sm text-gray-600">No Answer Rate</p>
            <p className="text-2xl font-bold text-red-600">{data.responseTimeAnalysis.notSolvableNoAnswerPercentage?.toFixed(1)}%</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SolvableAnalysisChart;
