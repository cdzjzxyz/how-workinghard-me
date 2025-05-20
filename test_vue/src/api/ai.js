import axios from 'axios'

export function analyzeAttendance(question) {
  return axios.post('/ai/analyzeAttendance', { question })
}