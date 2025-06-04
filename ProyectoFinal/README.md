{
	"info": {
		"_postman_id": "4fb38411-bb99-43fe-b46a-8cb0cdff1f40",
		"name": "JyAA",
		"schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json",
		"_exporter_id": "45502493",
		"_collection_link": "https://gastonamantea-5909913.postman.co/workspace/JyAA~d01a6b9e-6382-42a1-8427-b9469f1edad4/collection/45502436-4fb38411-bb99-43fe-b46a-8cb0cdff1f40?action=share&source=collection_link&creator=45502493"
	},
	"item": [
		{
			"name": "RolServlet",
			"item": [
				{
					"name": "Crear rol ADMIN",
					"request": {
						"method": "POST",
						"header": [],
						"url": {
							"raw": "localhost:8080/ProyectoFinal/rol?tipo=ADMINISTRADOR",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"rol"
							],
							"query": [
								{
									"key": "tipo",
									"value": "ADMINISTRADOR"
								}
							]
						}
					},
					"response": []
				},
				{
					"name": "Crear rol Org Social",
					"request": {
						"method": "POST",
						"header": [],
						"url": {
							"raw": "localhost:8080/ProyectoFinal/rol?tipo=ORGANIZACION_SOCIAL",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"rol"
							],
							"query": [
								{
									"key": "tipo",
									"value": "ORGANIZACION_SOCIAL"
								}
							]
						}
					},
					"response": []
				},
				{
					"name": "Crear rol Personal Salud",
					"request": {
						"method": "POST",
						"header": [],
						"url": {
							"raw": "localhost:8080/ProyectoFinal/rol?tipo=PERSONAL_SALUD",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"rol"
							],
							"query": [
								{
									"key": "tipo",
									"value": "PERSONAL_SALUD"
								}
							]
						}
					},
					"response": []
				},
				{
					"name": "Traer Roles",
					"request": {
						"method": "GET",
						"header": [],
						"url": {
							"raw": "localhost:8080/ProyectoFinal/rol",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"rol"
							]
						}
					},
					"response": []
				}
			]
		},
		{
			"name": "UsuarioServlet",
			"item": [
				{
					"name": "Crear usuario 1",
					"request": {
						"auth": {
							"type": "noauth"
						},
						"method": "POST",
						"header": [],
						"url": {
							"raw": "http://localhost:8080/ProyectoFinal/usuario?mail=gaston@gmail.com&password=1234&name=gas",
							"protocol": "http",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"usuario"
							],
							"query": [
								{
									"key": "mail",
									"value": "gaston@gmail.com"
								},
								{
									"key": "password",
									"value": "1234"
								},
								{
									"key": "name",
									"value": "gas"
								}
							]
						}
					},
					"response": []
				},
				{
					"name": "Crear usuario 2",
					"request": {
						"auth": {
							"type": "noauth"
						},
						"method": "POST",
						"header": [],
						"url": {
							"raw": "http://localhost:8080/ProyectoFinal/usuario?mail=nico@gmail.com&password=1234&name=nico",
							"protocol": "http",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"usuario"
							],
							"query": [
								{
									"key": "mail",
									"value": "nico@gmail.com"
								},
								{
									"key": "password",
									"value": "1234"
								},
								{
									"key": "name",
									"value": "nico"
								}
							]
						}
					},
					"response": []
				},
				{
					"name": "Crear usuario 3",
					"request": {
						"auth": {
							"type": "noauth"
						},
						"method": "POST",
						"header": [],
						"url": {
							"raw": "http://localhost:8080/ProyectoFinal/usuario?mail=manolito@gmail.com&password=1234&name=manolito",
							"protocol": "http",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"usuario"
							],
							"query": [
								{
									"key": "mail",
									"value": "manolito@gmail.com"
								},
								{
									"key": "password",
									"value": "1234"
								},
								{
									"key": "name",
									"value": "manolito"
								}
							]
						}
					},
					"response": []
				},
				{
					"name": "Asignar Admin",
					"request": {
						"auth": {
							"type": "noauth"
						},
						"method": "POST",
						"header": [],
						"url": {
							"raw": "http://localhost:8080/ProyectoFinal/usuarioRol?action=addRoleToUser&userId=1&rolId=1",
							"protocol": "http",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"usuarioRol"
							],
							"query": [
								{
									"key": "action",
									"value": "addRoleToUser"
								},
								{
									"key": "userId",
									"value": "1"
								},
								{
									"key": "rolId",
									"value": "1"
								}
							]
						}
					},
					"response": []
				},
				{
					"name": "Asignar Org Soc",
					"request": {
						"auth": {
							"type": "noauth"
						},
						"method": "POST",
						"header": [],
						"url": {
							"raw": "http://localhost:8080/ProyectoFinal/usuarioRol?action=addRoleToUser&userId=2&rolId=2",
							"protocol": "http",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"usuarioRol"
							],
							"query": [
								{
									"key": "action",
									"value": "addRoleToUser"
								},
								{
									"key": "userId",
									"value": "2"
								},
								{
									"key": "rolId",
									"value": "2"
								}
							]
						}
					},
					"response": []
				},
				{
					"name": "Asignar Per Salud",
					"request": {
						"auth": {
							"type": "noauth"
						},
						"method": "POST",
						"header": [],
						"url": {
							"raw": "http://localhost:8080/ProyectoFinal/usuarioRol?action=addRoleToUser&userId=3&rolId=3",
							"protocol": "http",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"usuarioRol"
							],
							"query": [
								{
									"key": "action",
									"value": "addRoleToUser"
								},
								{
									"key": "userId",
									"value": "3"
								},
								{
									"key": "rolId",
									"value": "3"
								}
							]
						}
					},
					"response": []
				},
				{
					"name": "Traer Usuarios",
					"request": {
						"auth": {
							"type": "noauth"
						},
						"method": "GET",
						"header": [],
						"url": {
							"raw": "http://localhost:8080/ProyectoFinal/usuario",
							"protocol": "http",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"usuario"
							]
						}
					},
					"response": []
				}
			]
		},
		{
			"name": "CampañaServlet",
			"item": [
				{
					"name": "Crear Campaña",
					"request": {
						"method": "POST",
						"header": [],
						"url": {
							"raw": "localhost:8080/ProyectoFinal/campania?name=Campaña 1&startDate=2024-10-10&endDate=2024-11-10",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"campania"
							],
							"query": [
								{
									"key": "name",
									"value": "Campaña 1"
								},
								{
									"key": "startDate",
									"value": "2024-10-10"
								},
								{
									"key": "endDate",
									"value": "2024-11-10"
								}
							]
						}
					},
					"response": []
				},
				{
					"name": "Traer Campañas",
					"request": {
						"method": "GET",
						"header": [],
						"url": {
							"raw": "localhost:8080/ProyectoFinal/campania",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"campania"
							],
							"query": [
								{
									"key": "",
									"value": "",
									"disabled": true
								}
							]
						}
					},
					"response": []
				}
			]
		},
		{
			"name": "EncuestadorServlet",
			"item": [
				{
					"name": "Crear Encuestador",
					"request": {
						"method": "POST",
						"header": [],
						"url": {
							"raw": "localhost:8080/ProyectoFinal/encuestador?action=create&name=Juan encuestador&dni=43988677&birthDate=10-04-2002&sex=male&occupation=Hace encuesta muy buenas",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"encuestador"
							],
							"query": [
								{
									"key": "action",
									"value": "create"
								},
								{
									"key": "name",
									"value": "Juan encuestador"
								},
								{
									"key": "dni",
									"value": "43988677"
								},
								{
									"key": "birthDate",
									"value": "10-04-2002"
								},
								{
									"key": "sex",
									"value": "male"
								},
								{
									"key": "occupation",
									"value": "Hace encuesta muy buenas"
								}
							]
						}
					},
					"response": []
				},
				{
					"name": "Traer encuestadores",
					"request": {
						"method": "GET",
						"header": [],
						"url": {
							"raw": "localhost:8080/ProyectoFinal/encuestador?action=findAll",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"encuestador"
							],
							"query": [
								{
									"key": "action",
									"value": "findAll"
								}
							]
						}
					},
					"response": []
				}
			]
		},
		{
			"name": "JornadaServlet",
			"item": [
				{
					"name": "Crear Jornada",
					"request": {
						"method": "POST",
						"header": [],
						"url": {
							"raw": "localhost:8080/ProyectoFinal/jornadas?action=create&name=jornadaPrimera&startDate=2024-10-10&campañaId=1&endDate=2024-11-05",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"jornadas"
							],
							"query": [
								{
									"key": "action",
									"value": "create"
								},
								{
									"key": "name",
									"value": "jornadaPrimera"
								},
								{
									"key": "startDate",
									"value": "2024-10-10"
								},
								{
									"key": "campañaId",
									"value": "1"
								},
								{
									"key": "endDate",
									"value": "2024-11-05"
								}
							]
						}
					},
					"response": []
				},
				{
					"name": "Asignar Encuestador",
					"request": {
						"method": "POST",
						"header": [],
						"url": {
							"raw": "localhost:8080/ProyectoFinal/jornadas?action=addEncuestador&jornadaId=1&encuestadorId=1",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"jornadas"
							],
							"query": [
								{
									"key": "action",
									"value": "addEncuestador"
								},
								{
									"key": "jornadaId",
									"value": "1"
								},
								{
									"key": "encuestadorId",
									"value": "1"
								}
							]
						}
					},
					"response": []
				},
				{
					"name": "Traer Jornadas",
					"request": {
						"method": "GET",
						"header": [],
						"url": {
							"raw": "localhost:8080/ProyectoFinal/jornadas",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"jornadas"
							]
						}
					},
					"response": []
				}
			]
		},
		{
			"name": "BarrioServlet",
			"item": [
				{
					"name": "Traer Barrios",
					"request": {
						"method": "GET",
						"header": [],
						"url": {
							"raw": "http://localhost:8080/ProyectoFinal/barrios",
							"protocol": "http",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"barrios"
							]
						}
					},
					"response": []
				},
				{
					"name": "Crear Barrio",
					"request": {
						"auth": {
							"type": "noauth"
						},
						"method": "POST",
						"header": [],
						"body": {
							"mode": "raw",
							"raw": "{\r\n  \"barrio\": {\r\n    \"name\": \"Barrio Nuevo 90\"\r\n  },\r\n  \"coordenadas\": [\r\n    {\r\n      \"latitud\": 100,\r\n      \"longitud\": 100\r\n    },\r\n    {\r\n      \"latitud\":50,\r\n      \"longitud\": 50\r\n    },\r\n        {\r\n      \"latitud\":1,\r\n      \"longitud\": 1\r\n    }\r\n  ]\r\n}",
							"options": {
								"raw": {
									"language": "json"
								}
							}
						},
						"url": {
							"raw": "http://localhost:8080/ProyectoFinal/barrios",
							"protocol": "http",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"barrios"
							]
						}
					},
					"response": []
				},
				{
					"name": "Editar Barrio",
					"request": {
						"auth": {
							"type": "noauth"
						},
						"method": "PUT",
						"header": [],
						"body": {
							"mode": "raw",
							"raw": "{\r\n  \"barrio\": {\r\n    \"name\": \"Barrio Nuevo Nombre2\"\r\n  },\r\n      \"coordenadas\": [\r\n        {\r\n            \"latitud\": 55.0,\r\n            \"longitud\": 55.0\r\n        },\r\n        {\r\n            \"latitud\": 599.0,\r\n            \"longitud\": 99.0\r\n        },\r\n        {\r\n            \"latitud\": 99.0,\r\n            \"longitud\": 99.0\r\n        }\r\n    ]\r\n}",
							"options": {
								"raw": {
									"language": "json"
								}
							}
						},
						"url": {
							"raw": "http://localhost:8080/ProyectoFinal/barrios?id=6",
							"protocol": "http",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"barrios"
							],
							"query": [
								{
									"key": "id",
									"value": "6"
								}
							]
						}
					},
					"response": []
				}
			]
		},
		{
			"name": "EncuestaServlet",
			"item": [
				{
					"name": "Traer encuestas",
					"request": {
						"method": "GET",
						"header": [],
						"url": {
							"raw": "http://localhost:8080/ProyectoFinal/encuesta",
							"protocol": "http",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"encuesta"
							]
						}
					},
					"response": []
				},
				{
					"name": "Crear encuesta",
					"request": {
						"auth": {
							"type": "noauth"
						},
						"method": "POST",
						"header": [],
						"body": {
							"mode": "raw",
							"raw": ""
						},
						"url": {
							"raw": "http://localhost:8080/ProyectoFinal/encuesta?tipo=PERSONA&viviendaId=1",
							"protocol": "http",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"encuesta"
							],
							"query": [
								{
									"key": "name",
									"value": "gastoncito",
									"disabled": true
								},
								{
									"key": "tipo",
									"value": "PERSONA"
								},
								{
									"key": "campañaId",
									"value": "1",
									"disabled": true
								},
								{
									"key": "viviendaId",
									"value": "1"
								}
							]
						}
					},
					"response": []
				},
				{
					"name": "Crear Renglon",
					"request": {
						"auth": {
							"type": "noauth"
						},
						"method": "POST",
						"header": [],
						"url": {
							"raw": "http://localhost:8080/ProyectoFinal/renglon?tipo=asd&valor=sd&encuestaId=2",
							"protocol": "http",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"renglon"
							],
							"query": [
								{
									"key": "tipo",
									"value": "asd"
								},
								{
									"key": "valor",
									"value": "sd"
								},
								{
									"key": "encuestaId",
									"value": "2"
								}
							]
						}
					},
					"response": []
				},
				{
					"name": "Asociar Renglon a Encuesta",
					"request": {
						"auth": {
							"type": "noauth"
						},
						"method": "PUT",
						"header": [],
						"url": {
							"raw": "http://localhost:8080/ProyectoFinal/encuesta?encuestaId=2&renglonId=1",
							"protocol": "http",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"encuesta"
							],
							"query": [
								{
									"key": "encuestaId",
									"value": "2"
								},
								{
									"key": "renglonId",
									"value": "1"
								}
							]
						}
					},
					"response": []
				}
			]
		},
		{
			"name": "ZonaServlet",
			"item": [
				{
					"name": "Traer zonas",
					"request": {
						"auth": {
							"type": "noauth"
						},
						"method": "GET",
						"header": [],
						"url": {
							"raw": "http://localhost:8080/ProyectoFinal/zonas",
							"protocol": "http",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"zonas"
							]
						}
					},
					"response": []
				},
				{
					"name": "Editar Zona",
					"request": {
						"auth": {
							"type": "noauth"
						},
						"method": "PUT",
						"header": [],
						"body": {
							"mode": "raw",
							"raw": "\r\n  {\r\n  \"zona\": {\r\n    \"color\":\"red\",\r\n    \"barrio\": {\r\n      \"id\": 5\r\n    }\r\n  },\r\n  \"coordenadas\": [\r\n    {\r\n      \"latitud\": -55,\r\n      \"longitud\": -58.381592\r\n    },\r\n    {\r\n      \"latitud\": -34.604722,\r\n      \"longitud\": -58.382592\r\n    },\r\n    {\r\n      \"latitud\": -34.605722,\r\n      \"longitud\": -58.383592\r\n    }\r\n  ]\r\n}",
							"options": {
								"raw": {
									"language": "json"
								}
							}
						},
						"url": {
							"raw": "http://localhost:8080/ProyectoFinal/zonas?id=8",
							"protocol": "http",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"zonas"
							],
							"query": [
								{
									"key": "id",
									"value": "8"
								}
							]
						}
					},
					"response": []
				},
				{
					"name": "Crear zona",
					"request": {
						"auth": {
							"type": "noauth"
						},
						"method": "POST",
						"header": [],
						"body": {
							"mode": "raw",
							"raw": "\r\n  {\r\n  \"zona\": {\r\n    \"color\":\"redOriginal\",\r\n    \"barrio\": {\r\n      \"id\": 5\r\n    }\r\n  },\r\n  \"coordenadas\": [\r\n    {\r\n      \"latitud\": -1,\r\n      \"longitud\": -2\r\n    },\r\n    {\r\n      \"latitud\": -3,\r\n      \"longitud\": -4\r\n    },\r\n        {\r\n      \"latitud\": -5,\r\n      \"longitud\": -6\r\n    }\r\n  ]\r\n}",
							"options": {
								"raw": {
									"language": "json"
								}
							}
						},
						"url": {
							"raw": "http://localhost:8080/ProyectoFinal/zonas",
							"protocol": "http",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"ProyectoFinal",
								"zonas"
							]
						}
					},
					"response": []
				}
			]
		}
	]
}