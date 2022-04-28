import Axios from "axios";

const LOCAL_API_URL = "http://localhost:8080";
const SERVER_API_URL = "http://k6c206.p.ssafy.io:8000/advent-server";

const allAxios = Axios.create({
  baseURL: `${SERVER_API_URL}`,
});

export default allAxios;
