import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'

// 基础URL，统一管理
export const BASE_URL = 'http://localhost:8080'

// 创建axios实例
const service: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: 100000, // 请求超时时间
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 可以在这里添加请求头等操作
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    return response.data
  },
  (error) => {
    return Promise.reject(error)
  },
)

// 封装请求方法
const request = {
  get<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return service.get(url, config)
  },
  post<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    return service.post(url, data, config)
  },
  put<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    return service.put(url, data, config)
  },
  delete<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return service.delete(url, config)
  },
}

// 定义请求参数类型，与后端实体类保持一致
export interface Recommend {
  budget: string
  days: string
  city: string
}

export interface UserAuthPayload {
  id: string
  name?: string
  pwd: string
}

export interface AuthUser {
  id: string
  name: string
  logo?: string | null
}

// 后端接口直接返回 AI 生成的文本内容
export type TravelRecommendResponse = string

// 旅游推荐接口,通过AI规划行程、温馨提示
export const getTravelRecommend = (params: Recommend) => {
  return request.post<TravelRecommendResponse>('/recommend/planning', params)
}

// 获取所有城市接口
export const getAllCity = () => {
  return request.get<string[]>('/city/getAllCity')
}

// AI聊天功能（流式响应）
export const aiChat = (query: string) => {
  return fetch(`${BASE_URL}/recommend/chat?query=${encodeURIComponent(query)}`, {
    headers: {
      Accept: 'text/event-stream, text/plain, */*',
    },
  })
}

export const loginUser = (params: UserAuthPayload) => {
  return request.post<AuthUser>('/user/login', params)
}

export const registerUser = (params: UserAuthPayload) => {
  return request.post<AuthUser>('/user/register', params)
}


export default request
