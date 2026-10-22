
import axios from 'axios'

const api = axios.create({
  baseURL: '/api'
})

export const getPlants = () => api.get('/plants')
export const getPlant = (id) => api.get(`/plants/${id}`)
export const waterPlant = (id) => api.post(`/plants/${id}/water`)
